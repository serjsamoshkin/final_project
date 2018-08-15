package service.reception;

import dao.DaoMapper;
import dao.model.MasterDAO;
import dao.model.ServiceDAO;
import entity.model.Master;
import entity.model.Service;
import myPersistenceSystem.PersistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.AbstractService;
import service.dto.reception.ShowReceptionInDto;
import service.dto.reception.ShowReceptionOutDto;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReceptionService extends AbstractService {

    private final Logger logger = LogManager.getLogger(ReceptionService.class);

    public ReceptionService(ServletContext context, DataSource dataSource) {
        super(context, dataSource);
    }

    private Map<Master, Map<Date, Boolean>> getMastersSchedule(LocalDate date, Service service){

        Map<Master, Map<Date, Boolean>> schedule = new TreeMap<>();

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

    private Map<Date, Boolean> getDailyScheme(Master master, LocalDate date){

        Map<Date, Boolean> schedule = new TreeMap<>();
        for (int i = 0; i < 9; i++) {
            schedule.put(java.sql.Timestamp.valueOf(LocalDateTime.of(date, LocalTime.of(9+i,0))), false);
        }

        return schedule;
    }

    public Optional<Master> getMaster(int id){
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

    public ShowReceptionOutDto processShowReceptionRequest(ShowReceptionInDto inDto){

        ShowReceptionOutDto.ShowReceptionOutDtoBuilder builder = ShowReceptionOutDto.getBuilder();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate day = LocalDate.now();
        if (inDto.getDay().isPresent()){
            try {
                day =  LocalDate.parse(inDto.getDay().get(), dateFormat);
            }catch (Exception e){
                logger.error("Incorrect day format in ShowReceptionCommand", e);

                return builder.buildFalse();
            }
        }

        Optional<Service> serviceOpt = Optional.of(Service.EMPTY_SERVICE);
        if (inDto.getService().isPresent()){
            try {
                serviceOpt =  getServiceById(Integer.valueOf(inDto.getService().get()));
                if (!serviceOpt.isPresent()) {
                    logger.error("Incorrect filter_service_opt format in ShowReceptionCommand");
                    return builder.buildFalse();
                }
            }catch (Exception e){
                logger.error("Incorrect filter_service_opt format in ShowReceptionCommand", e);
                return builder.buildFalse();
            }
        }

        builder.setMastersSchedule(getMastersSchedule(day, serviceOpt.get()));

        builder.setServiceMap(getServiceMap(serviceOpt.get()));

        builder.setReservationDay(java.sql.Date.valueOf(day));
        builder.setReservationDayTxt(dateFormat.format(day));
        builder.setNextDay(dateFormat.format(day.plusDays(1)));
        LocalDate previousDate = day.minusDays(1);
        if (previousDate.isBefore(LocalDate.now())){
            builder.setPreviousDay("");
        }else {
            builder.setPreviousDay(dateFormat.format(previousDate));
        }

        return builder.build();
    }

}


