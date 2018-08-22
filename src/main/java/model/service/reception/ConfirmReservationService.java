package model.service.reception;

import model.dao.DaoMapper;
import model.dao.reception.ReceptionDAO;
import model.entity.authentication.User;
import model.entity.reception.Reception;
import model.service.AbstractService;
import model.service.ServiceMapper;
import model.service.util.DataCheckerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.RowNotUniqueException;
import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;
import util.dto.reception.ProcessReservation.ProcessReceptionOutDto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Optional;

public class ConfirmReservationService extends AbstractService {

    private DataCheckerService dataChecker = ServiceMapper.getMapper().getService(DataCheckerService.class);

    private final Logger logger = LogManager.getLogger(ConfirmReservationService.class);

    public ConfirmReservationService(DataSource dataSource) {
        super(dataSource);
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
            logger.info(e);
            return false;
        }catch (SQLException e){
            logger.error(e);
            return false;
        }

    }




}
