package service.reception;

import dao.DaoMapper;
import dao.model.MasterDAO;
import dao.model.ReceptionDAO;
import dao.model.ServiceDAO;
import entity.authentication.User;
import entity.model.Master;
import entity.model.Reception;
import entity.model.Service;
import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.AbstractService;
import service.dto.reception.ProcessReservation.ProcessReceptionInDto;
import service.dto.reception.ProcessReservation.ProcessReceptionOutDto;
import service.dto.reception.ShowReception.ShowReceptionInDto;
import service.dto.reception.ShowReception.ShowReceptionOutDto;
import util.LocalDateTimeFormatter;
import util.properties.DateTimePatternsPropertiesReader;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

        Set<Service> masterServices = master.getMasterServices();
        Map<Service, Boolean> serviceMap;
        if (masterServices.contains(service) || service == Service.EMPTY_SERVICE) {
            serviceMap = masterServices.stream().collect(Collectors.toMap(Function.identity(), s -> s.equals(service)));
        }else {
            return builder.buildFalse();
        }

        Map<LocalTime, Boolean> dailyMasterScheme =  getTimeDailyScheme(master, date);

        if (dailyMasterScheme.containsKey(time)){
            LocalTime endTime = time.plusHours(serviceOpt.get().getDuration());
            long reserved = dailyMasterScheme.entrySet().stream()
                    .filter(e -> e.getKey().equals(time) || (e.getKey().isAfter(time) && e.getKey().isBefore(endTime)))
                    .filter(Map.Entry::getValue).count();

            if (reserved > 0){
                builder.setReserved(true);
            }
        }else {
            return builder.buildFalse();
        }

        builder.setMaster(master);
        builder.setDate(inDto.getDay().get());
        builder.setTime(inDto.getTime().get());
        builder.setServiceMap(serviceMap);
        builder.setService(service);

        return builder.build();

    }

    public boolean confirmReservation(ProcessReceptionOutDto receptionDto, User user){

        Reception reception = new Reception();
        reception.setDay(LocalDateTimeFormatter.toSqlDate(receptionDto.getDate()));
        reception.setTime(LocalDateTimeFormatter.toSqlTime(receptionDto.getTime()));

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
                        LocalDateTimeFormatter.toLocalTime(receptionDto.getTime()).plusHours(receptionDto.getService().getDuration()),
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

        Map<Master, Map<String, Boolean>> schedule = new TreeMap<>();

        try (Connection connection = getDataSource().getConnection()){

            // TODO запрос в цикле!!!!!
            /*
            Так как у нас есть таблица записей(receptions), то будет логично получить ее один раз,
            и ее уже прикрутить к сгенерированной мапе по мастерам
             */
            if (service == Service.EMPTY_SERVICE){
                DaoMapper.getMapper().getDao(MasterDAO.class)
                        .getALL(connection).forEach(m -> schedule.put(m, getDailyScheme(m, date)));
            }else {
                DaoMapper.getMapper().getDao(MasterDAO.class)
                        .getMasterListByService(service, connection).forEach(m -> schedule.put(m, getDailyScheme(m, date)));
            }

        }catch (SQLException e){
            throw new PersistException(e);
        }

        return schedule;

    }

    // TODO вызвается в цикле, необходимо этот метод оставить только для проверки диапазона
    private Map<String, Boolean> getDailyScheme(Master master, LocalDate date){

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(
                DateTimePatternsPropertiesReader.getInstance().getPropertyValue("time_pattern"));

        Map<String, Boolean> schedule = new LinkedHashMap<>();
        for (int i = 0; i < 9; i++) {
            schedule.put(fmt.format(LocalTime.of(9+i,0)), false);
        }

        return schedule;
    }
    private Map<LocalTime, Boolean> getTimeDailyScheme(Master master, LocalDate date){

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(
                DateTimePatternsPropertiesReader.getInstance().getPropertyValue("time_pattern"));

        Map<LocalTime, Boolean> schedule = new LinkedHashMap<>();
        for (int i = 0; i < 9; i++) {
            schedule.put(LocalTime.of(9+i,0), false);
        }

        return schedule;
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

    private Map<Service, Boolean> getServiceMap(Service serviceOpt){

        Map<Service, Boolean> servMap = new TreeMap<>();

        try (Connection connection = getDataSource().getConnection()){

            DaoMapper.getMapper().getDao(ServiceDAO.class)
                    .getALL(connection).forEach(s -> servMap.put(s, s.equals(serviceOpt)));

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

}


