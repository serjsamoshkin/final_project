package model.service.reception;

import model.dao.DaoMapper;
import model.dao.reception.MasterDAO;
import model.dao.reception.ReceptionDAO;
import model.dao.reception.ServiceDAO;
import model.entity.reception.Master;
import model.entity.reception.Reception;
import model.entity.reception.Service;
import model.service.ServiceMapper;
import model.service.util.DataCheckerService;
import persistenceSystem.PersistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.service.AbstractService;
import util.dto.reception.ShowReceptionInDto;
import util.dto.reception.ShowReceptionOutDto;
import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShowReceptionService extends AbstractService {

    private final Logger logger = LogManager.getLogger(ShowReceptionService.class);

    private DataCheckerService dataChecker = ServiceMapper.getMapper().getService(DataCheckerService.class);

    public ShowReceptionService(DataSource dataSource) {
        super(dataSource);
    }

    public ShowReceptionOutDto processShowReceptionRequest(ShowReceptionInDto inDto){

        ShowReceptionOutDto.ShowReceptionOutDtoBuilder builder = ShowReceptionOutDto.getBuilder();

        Optional<LocalDate> optDay = dataChecker.checkDate(inDto.getDay(), LocalDate.now());
        LocalDate date;
        if (optDay.isPresent()){
            date = optDay.get();
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

    private Map<Master, Map<String, Boolean>> getMastersSchedule(LocalDate date, Service service){

        Map<Master, Map<String, Boolean>> schedule;
        List<Reception> receptions;
        List<Master> masters;

        try (Connection connection = getDataSource().getConnection()){


            if (service == Service.EMPTY_SERVICE){
                masters = DaoMapper.getMapper().getDao(MasterDAO.class).getALL(connection);
                receptions = DaoMapper.getMapper().getDao(ReceptionDAO.class).getMastersReceptions(date, List.of(), connection);
            }else {
                masters = DaoMapper.getMapper().getDao(MasterDAO.class).getMasterListByService(service, connection);
                receptions = DaoMapper.getMapper().getDao(ReceptionDAO.class).getMastersReceptions(date, masters, connection);
            }

        }catch (SQLException e){
            throw new PersistException(e);
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
                masterSchedule.computeIfPresent(LocalDateTimeFormatter.toString(TimePlanning.plusDuration(startTime, i)), ShowReceptionService::alwaysTrue);
            }
                /* Reverse blocking: the user can't reserve this service if the period between the
                specified time and the current reception time is less than the duration of the service.*/
            for (int i = 1; i < service.getDuration(); i++) {
                masterSchedule.computeIfPresent(LocalDateTimeFormatter.toString(TimePlanning.minusDuration(startTime, i)), ShowReceptionService::alwaysTrue);
            }
        }

        for (Master master : masters) {
            /* Reverse blocking: from end of day.*/
            for (int i = 1; i < service.getDuration(); i++) {
                schedule.get(master).computeIfPresent(LocalDateTimeFormatter.toString(TimePlanning.minusDuration(TimePlanning.endOfDay(date), i)), ShowReceptionService::alwaysTrue);
            }
        }

        return schedule;

    }

    /**
     * Returns a services with a mark where parameter service == service in map
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

    private Map<String, Boolean> getDailyScheme(LocalDate date){
        return TimePlanning.getDailyScheme(date).stream().map(LocalDateTimeFormatter::toString).collect(
                Collectors.toMap(Function.identity(), ShowReceptionService::alwaysFalse,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
    }

    private static Boolean alwaysTrue(Object... o) {
        return true;
    }

    private static Boolean alwaysFalse(Object... o) {
        return false;
    }

}


