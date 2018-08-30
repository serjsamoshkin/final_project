package model.service.util;

import model.dao.DaoMapper;
import model.dao.reception.MasterDAO;
import model.dao.reception.ServiceDAO;
import model.entity.authentication.User;
import model.entity.reception.Master;
import model.entity.reception.Service;
import model.service.AbstractService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.PersistException;
import util.datetime.LocalDateTimeFormatter;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class DataCheckerService extends AbstractService{

    private static final Logger logger = LogManager.getLogger(DataCheckerService.class);

    public DataCheckerService(DataSource dataSource) {
        super(dataSource);
    }

    public Optional<LocalDate> checkDate(Optional<String> dayOpt, LocalDate def){

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

    public Optional<LocalTime> checkTime(Optional<String> timeOpt){

        if (timeOpt.isPresent()){
            try {
                return Optional.of(LocalDateTimeFormatter.toLocalTime(timeOpt.get()));
            }catch (Exception e){
                logger.error("Incorrect time format of time: " + timeOpt, e);
                return Optional.empty();
            }
        }else {
            logger.error("Empty time value in ProcessReservationCommand");
            return Optional.empty();
        }
    }


    public Optional<Integer> checkInteger(Optional<String> strOpt){

        if (strOpt.isPresent()){
            try {
                return Optional.of(Integer.valueOf(strOpt.get()));
            }catch (NumberFormatException e){
                logger.error(e);
                return Optional.empty();
            }
        }else {
            return Optional.empty();
        }
    }


    public Optional<Master> checkMaster(Optional<String> masterOpt){

        if (masterOpt.isPresent()){
            try {
                Optional<Master> master = getMasterById(Integer.valueOf(masterOpt.get()));//getMaster(Integer.valueOf(masterOpt.get()));
                if (master.isPresent()){
                    return master;
                }else {
                    throw new Exception();
                }
            }catch (Exception e){
                logger.error("Incorrect master Id: " + masterOpt.get(), e);
                return Optional.empty();
            }
        }else {
            logger.error("Empty master value");
            return Optional.empty();
        }
    }

    public Optional<Service> checkService(Optional<String> serviceOpt, Service def) {
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

    private Optional<Service> getServiceById(int id){

        try (Connection connection = getDataSource().getConnection()){
            return Optional.ofNullable(DaoMapper.getMapper().getDao(ServiceDAO.class).getByPK(id, connection));
        }catch (SQLException e){
            throw new PersistException(e);
        }
    }

    private Optional<Master> getMasterById(int id){
        Optional<Master> master;
        try (Connection connection = getDataSource().getConnection()){
            master = Optional.ofNullable(DaoMapper.getMapper().getDao(MasterDAO.class).getByPK(id, connection));
        }catch (SQLException e){
            throw new PersistException(e);
        }

        return master;
    }



}
