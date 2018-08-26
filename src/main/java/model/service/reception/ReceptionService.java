package model.service.reception;

import model.dao.DaoMapper;
import model.dao.reception.ReceptionDAO;
import model.entity.reception.Reception;
import model.service.AbstractService;
import model.service.ServiceMapper;
import model.service.review.ReviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.PersistException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;

public class ReceptionService extends AbstractService {

    private final Logger logger = LogManager.getLogger(ReceptionService.class);

    public ReceptionService(DataSource dataSource) {
        super(dataSource);
    }

    public boolean safeUpdate(Reception reception, Reception.Status status, int version, Connection connection){

        if (reception.getId() == 0) {
            throw new IllegalArgumentException("Reception not persisted");
        }
        if (status == null) {
            throw new NullPointerException("Status is null");
        }

        Reception copy = Reception.of(reception);
        copy.setStatus(status);
        copy.setVersion(version);

        try {
            connection.setAutoCommit(false);
            DaoMapper.getMapper().getDao(ReceptionDAO.class).update(copy, connection);
            boolean reviewOk = true;
            if (reception.getStatus() != Reception.Status.DONE && copy.getStatus() == Reception.Status.DONE) {
                reviewOk = ServiceMapper.getMapper().getService(ReviewService.class).createReview(reception, connection);
            }
            if (reviewOk) {
                connection.commit();
                connection.setAutoCommit(true);
            }else {
                connection.rollback();
                return false;
            }

        }catch (ConcurrentModificationException e){
            logger.error(e);
            return false;
        }
        catch (SQLException e){
            logger.error(e);
            return false;
        }catch (PersistException e){
            logger.error(e);
            return false;
        }

        return true;
    }

}
