package model.service.reception;

import model.dao.DaoMapper;
import model.dao.reception.MasterDAO;
import model.dao.reception.ReceptionDAO;
import model.dao.reception.ServiceDAO;
import model.entity.authentication.User;
import model.entity.reception.Master;
import model.entity.reception.Reception;
import model.entity.reception.Service;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.service.AbstractService;
import util.dto.reception.ProcessReservation.ProcessReceptionInDto;
import util.dto.reception.ProcessReservation.ProcessReceptionOutDto;
import util.dto.reception.ShowReception.ShowReceptionInDto;
import util.dto.reception.ShowReception.ShowReceptionOutDto;
import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReceptionService extends AbstractService {

    private final Logger logger = LogManager.getLogger(ReceptionService.class);

    public ReceptionService(ServletContext context, DataSource dataSource) {
        super(context, dataSource);
    }

    public ShowReceptionOutDto processShowReceptionRequest(ShowReceptionInDto inDto){

        ShowReceptionOutDto.ShowReceptionOutDtoBuilder builder = ShowReceptionOutDto.getBuilder();

        Optional<LocalDate> optDay = checkDate(inDto.getDay(), LocalDate.now());
        LocalDate date;
        if (optDay.isPresent()){
            date = optDay.get();
        }else {
            return builder.buildFalse();
        }

        Optional<Service> serviceOpt = checkService(inDto.getService(), Service.EMPTY_SERVICE);
        Service service;
        if (serviceOpt.isPresent()){
            service = serviceOpt.get();
        }else {
            return builder.buildFalse();
        }

        builder.setMastersSchedule(getMastersSchedule(date, service));

        builder.setServiceMap(getServiceMap(service));

        builder.setHours(TimePlanning.getDurationInHours(service.getDuration()));
        builder.setMinutes(TimePlanning.getDurationInMinutes(service.getDuration()));

        builder.setReservationDay(LocalDateTimeFormatter.toString(date));
        builder.setNextDay(LocalDateTimeFormatter.toString(date.plusDays(1)));
        LocalDate previousDate = date.minusDays(1);
        if (previousDate.isBefore(LocalDate.now())){
            builder.setPreviousDay("");
        }else {
            builder.setPreviousDay(LocalDateTimeFormatter.toString(previousDate));
        }

        return builder.build();
    }

    public ProcessReceptionOutDto processReservationRequest(ProcessReceptionInDto inDto){

        ProcessReceptionOutDto.ShowReceptionOutDtoBuilder builder = ProcessReceptionOutDto.getBuilder();

        Optional<LocalDate> optDay = checkDate(inDto.getDay(), LocalDate.now());
        LocalDate date;
        if (optDay.isPresent()){
            date = optDay.get();
        }else {
            return builder.buildFalse();
        }

        Optional<LocalTime> timeOpt = checkTime(inDto.getTime());
        LocalTime time;
        if (timeOpt.isPresent()){
            time = timeOpt.get();
        }else {
            return builder.buildFalse();
        }

        Master master;
        Optional<Master> masterOpt = checkMaster(inDto.getMaster());
        if (masterOpt.isPresent()){
            master = masterOpt.get();
        }else {
            return builder.buildFalse();
        }

        Optional<Service> serviceOpt = checkService(inDto.getService(), Service.EMPTY_SERVICE);
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


        Map<String, Boolean> dailyMasterScheme =  getDailyScheme(date);

        if (dailyMasterScheme.containsKey(LocalDateTimeFormatter.toString(time))){
            LocalTime endTime = TimePlanning.plusDuration(time, serviceOpt.get().getDuration());

            try (Connection connection = getDataSource().getConnection()){
                if (time.isBefore(TimePlanning.startOfDay(date)) ||  endTime.isAfter(TimePlanning.endOfDay(date))){
                    builder.setReserved(true);
                }else {
                    builder.setReserved(
                            DaoMapper.getMapper().getDao(ReceptionDAO.class).
                                    checkReservationInSchedule(date, time, endTime, master, Optional.empty(), connection));
                }
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
            // TODO вернуть признак занятости
            logger.error(e);
            return false;
        }catch (SQLException e){
            logger.error(e);
            return false;
        }

    }


    private Map<Master, Map<String, Boolean>> getMastersSchedule(LocalDate date, Service service){

        Map<Master, Map<String, Boolean>> schedule;
        List<Reception> receptions;

        try (Connection connection = getDataSource().getConnection()){

            List<Master> masters;
            if (service == Service.EMPTY_SERVICE){
                masters = DaoMapper.getMapper().getDao(MasterDAO.class).getALL(connection);
                receptions = DaoMapper.getMapper().getDao(ReceptionDAO.class).getMastersReceptions(date, List.of(), connection);
            }else {
                masters = DaoMapper.getMapper().getDao(MasterDAO.class).getMasterListByService(service, connection);
                receptions = DaoMapper.getMapper().getDao(ReceptionDAO.class).getMastersReceptions(date, masters, connection);
            }

            schedule = masters.stream().collect(Collectors.toMap(Function.identity(), m -> getDailyScheme(date)));

            receptions.sort(Comparator.comparing(Reception::getMaster));

            Master tmp = Master.EMPTY_MASTER;
            Map<String, Boolean> masterSchedule = Map.of();

            for (Reception reception : receptions) {
                LocalTime startTime = LocalDateTimeFormatter.toLocalTime(reception.getTime());
                LocalTime endTime = LocalDateTimeFormatter.toLocalTime(reception.getEndTime());

                if (tmp != reception.getMaster()) {
                    tmp = reception.getMaster();
                    masterSchedule = schedule.get(tmp);
                }

                /*Direct blocking: block reservations in between the start and end of the service (for master of the service).*/
                schedule.get(reception.getMaster()).put(LocalDateTimeFormatter.toString(startTime), true);
                for (int i = 1; i < TimePlanning.betweenDuration(startTime, endTime); i++) {
                    masterSchedule.computeIfPresent(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(startTime, i)), ReceptionService::apply);
                }
                /* Reverse blocking: the user can't reserve this service if the period between the
                specified time and the current reception time is less than the duration of the service.*/
                for (int i = 1; i < service.getDuration(); i++) {
                    masterSchedule.computeIfPresent(LocalDateTimeFormatter.toString(TimePlanning.minusDuration(startTime, i)), ReceptionService::apply);
                }
            }

            for (Master master : masters) {
                /* Reverse blocking: from end of day.*/
                for (int i = 1; i < service.getDuration(); i++) {
                    schedule.get(master).computeIfPresent(LocalDateTimeFormatter.toString(TimePlanning.minusDuration(TimePlanning.endOfDay(date), i)), ReceptionService::apply);
                }
            }


        }catch (SQLException e){
            throw new PersistException(e);
        }

        return schedule;

    }

    private Map<String, Boolean> getDailyScheme(LocalDate date){

        Map<String, Boolean> schedule = new LinkedHashMap<>();
        for (int i = 0; i < TimePlanning.betweenDuration(TimePlanning.startOfDay(date), TimePlanning.endOfDay(date)); i++) {
            schedule.put(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(TimePlanning.startOfDay(date), i)), false);
        }

        return schedule;
    }

    private Set<Service> getMasterServiceForDayAndTime(LocalDate date, LocalTime time, Master master){
        try (Connection connection = getDataSource().getConnection()){
            return DaoMapper.getMapper().getDao(ReceptionDAO.class).getServiceListForTimeAndMaster(date, time, master, connection);
         }catch (SQLException e){
            throw new PersistException(e);
        }

    }

    private Optional<Master> getMaster(int id){
        Optional<Master> master;
        try (Connection connection = getDataSource().getConnection()){
            master = Optional.ofNullable(DaoMapper.getMapper().getDao(MasterDAO.class).getByPK(id, connection));
        }catch (SQLException e){
            throw new PersistException(e);
        }

        return master;

    }

    /**
     * Returns a services with a mark where parameter service == service in re
     * @param service user-selected service
     * @return {@code Service} map marked 'true' where service == service in map
     */
    private Map<Service, Boolean> getServiceMap(Service service){

        Map<Service, Boolean> servMap = new TreeMap<>();

        try (Connection connection = getDataSource().getConnection()){

            DaoMapper.getMapper().getDao(ServiceDAO.class)
                    .getALL(connection).forEach(s -> servMap.put(s, s.equals(service)));

        }catch (SQLException e){
            throw new PersistException(e);
        }

        return servMap;

    }

    private Optional<Service> getServiceById(int id){

        try (Connection connection = getDataSource().getConnection()){
            return Optional.ofNullable(DaoMapper.getMapper().getDao(ServiceDAO.class).getByPK(id, connection));
        }catch (SQLException e){
            throw new PersistException(e);
        }
    }

    private Optional<LocalDate> checkDate(Optional<String> dayOpt, LocalDate def){

        if (dayOpt.isPresent()){
            try {
                return Optional.of(LocalDateTimeFormatter.toLocalDate(dayOpt.get()));
            }catch (Exception e){
                logger.error("Incorrect day format of date: " + dayOpt.get() , e);
                return Optional.empty();
            }
        }else {
            return Optional.ofNullable(def);
        }
    }

    private Optional<LocalTime> checkTime(Optional<String> timeOpt){

        if (timeOpt.isPresent()){
            try {
                return Optional.of(LocalDateTimeFormatter.toLocalTime(timeOpt.get()));
            }catch (Exception e){
                logger.error("Incorrect day format in ProcessReservationCommand", e);
                return Optional.empty();
            }
        }else {
            logger.error("Empty time value in ProcessReservationCommand");
            return Optional.empty();
        }
    }

    private Optional<Master> checkMaster(Optional<String> masterOpt){

        if (masterOpt.isPresent()){
            try {
                Optional<Master> master = getMaster(Integer.valueOf(masterOpt.get()));
                if (master.isPresent()){
                    return master;
                }else {
                    throw new Exception();
                }
            }catch (Exception e){
                logger.error("Incorrect day format in ProcessReservationCommand", e);
                return Optional.empty();
            }
        }else {
            logger.error("Empty master value");
            return Optional.empty();
        }
    }

    private Optional<Service> checkService(Optional<String> serviceOpt, Service def) {
        if (serviceOpt.isPresent()) {
            try {
                Optional<Service> service = getServiceById(Integer.valueOf(serviceOpt.get()));
                if (service.isPresent()) {
                    return service;
                } else {
                    logger.error("Incorrect filter_service_opt format: " + serviceOpt);
                    return Optional.empty();
                }
            } catch (Exception e) {
                logger.error("Incorrect filter_service_opt format: " + serviceOpt, e);
                return Optional.empty();
            }
        }else {
            return Optional.ofNullable(def);
        }
    }

    private static Boolean apply(Object... o) {
        return true;
    }
}


