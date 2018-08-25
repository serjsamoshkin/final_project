package model.service.reception;

import model.dao.DaoMapper;
import model.dao.reception.ReceptionDAO;
import model.entity.authentication.User;
import model.entity.reception.Master;
import model.entity.reception.Reception;
import model.entity.reception.Service;
import model.service.AbstractService;
import model.service.ServiceMapper;
import model.service.util.DataCheckerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;
import util.dto.reception.ProcessReceptionInDto;
import util.dto.reception.ProcessReceptionOutDto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReservationService extends AbstractService {

    private DataCheckerService dataChecker = ServiceMapper.getMapper().getService(DataCheckerService.class);

    private final Logger logger = LogManager.getLogger(ReservationService.class);

    public ReservationService(DataSource dataSource) {
        super(dataSource);
    }

    public ProcessReceptionOutDto processReservationRequest(ProcessReceptionInDto inDto){

        ProcessReceptionOutDto.ShowReceptionOutDtoBuilder builder = ProcessReceptionOutDto.getBuilder();

        Optional<LocalDate> optDay = dataChecker.checkDate(inDto.getDay(), LocalDate.now());
        LocalDate date;
        if (optDay.isPresent()){
            date = optDay.get();
        }else {
            return builder.buildFalse();
        }

        Optional<LocalTime> timeOpt = dataChecker.checkTime(inDto.getTime());
        LocalTime time;
        if (timeOpt.isPresent()){
            time = timeOpt.get();
        }else {
            return builder.buildFalse();
        }

        Master master;
        Optional<Master> masterOpt = dataChecker.checkMaster(inDto.getMaster());
        if (masterOpt.isPresent()){
            master = masterOpt.get();
        }else {
            return builder.buildFalse();
        }

        Optional<Service> serviceOpt = dataChecker.checkService(inDto.getService(), Service.EMPTY_SERVICE);
        Service service;
        if (serviceOpt.isPresent()){
            service = serviceOpt.get();
        }else {
            return builder.buildFalse();
        }

        Map<Service, Boolean> serviceMap;
        Set<Service> serviceSet = getMasterServiceForDayAndTime(date, time, master);
        if (serviceSet.contains(service) || service == Service.EMPTY_SERVICE) {
            serviceMap = serviceSet.stream().collect(Collectors.toMap(Function.identity(), s -> s.equals(service)));
        } else {
            return builder.buildFalse();
        }

        if (TimePlanning.getDailyScheme(date).contains(time)){
            LocalTime endTime = TimePlanning.plusDuration(time, serviceOpt.get().getDuration());
            if (time.isBefore(TimePlanning.startOfDay(date)) ||  endTime.isAfter(TimePlanning.endOfDay(date))){
                builder.setReserved(true);
            }
            try (Connection connection = getDataSource().getConnection()){
                builder.setReserved(
                        DaoMapper.getMapper().getDao(ReceptionDAO.class).
                                checkReservationInSchedule(date, time, endTime, master, Optional.empty(), connection));
            }catch (SQLException e){
                logger.error(e);
                throw new PersistException(e);
            }

        }else {
            return builder.buildFalse();
        }

        builder.setMaster(master);
        builder.setDate(inDto.getDay().get());
        builder.setTime(inDto.getTime().get());
        builder.setServiceMap(serviceMap);
        builder.setService(service);

        builder.setHours(TimePlanning.getDurationInHours(service.getDuration()));
        builder.setMinutes(TimePlanning.getDurationInMinutes(service.getDuration()));
        builder.setEndTime(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(time, service.getDuration())));

        return builder.build();

    }

    public boolean confirmReservation(ProcessReceptionOutDto receptionDto, User user){

        Reception reception = new Reception();
        reception.setDay(LocalDateTimeFormatter.toSqlDate(receptionDto.getDate()));
        reception.setTime(LocalDateTimeFormatter.toSqlTime(receptionDto.getTime()));

        LocalTime localEndTime = TimePlanning.plusDuration(LocalDateTimeFormatter.toLocalTime(receptionDto.getTime()),
                receptionDto.getService().getDuration());
        reception.setEndTime(LocalDateTimeFormatter.toSqlTime(localEndTime));

        reception.setMaster(receptionDto.getMaster());
        reception.setService(receptionDto.getService());

        reception.setStatus(Reception.Status.NEW);

        reception.setUser(user);

        try (Connection connection = getDataSource().getConnection()){
            connection.setAutoCommit(false);
            DaoMapper.getMapper().getDao(ReceptionDAO.class).save(reception, connection);
            // TODO надежнее было бы заблочить записи в БД
            synchronized (ReceptionDAO.class) {
                boolean reserved = DaoMapper.getMapper().getDao(ReceptionDAO.class).checkReservationInSchedule(
                        LocalDateTimeFormatter.toLocalDate(receptionDto.getDate()),
                        LocalDateTimeFormatter.toLocalTime(receptionDto.getTime()),
                        localEndTime,
                        receptionDto.getMaster(),
                        Optional.of(reception.getId()),
                        connection);
                if (reserved) {
                    connection.rollback();
                    return false;
                }else {
                    connection.commit();
                }
            }

            connection.setAutoCommit(true);
            return true;
        }catch (RowNotUniqueException e){
            logger.info(e);
            return false;
        }catch (SQLException e){
            logger.error(e);
            return false;
        }

    }

    private Set<Service> getMasterServiceForDayAndTime(LocalDate date, LocalTime time, Master master){
        try (Connection connection = getDataSource().getConnection()){
            return DaoMapper.getMapper().getDao(ReceptionDAO.class).getServiceListForTimeAndMaster(date, time, master, connection);
        }catch (SQLException e){
            throw new PersistException(e);
        }
    }


}
