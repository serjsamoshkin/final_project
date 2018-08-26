package model.dao.model;

import model.dao.DaoMapper;
import model.dao.reception.ReceptionDAO;
import model.dao.reception.ReviewDAO;
import model.entity.reception.Reception;
import model.entity.reception.Review;
import model.service.ServiceMapper;
import model.service.review.ReviewService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import persistenceSystem.sql.MySqlJDBCDaoController;
import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReceptionTests {

    private static Connection connection;

    @Before
    public void setConnection() throws SQLException{
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/beauty_saloon?autoReconnect=true&useSSL=false&useUnicode=true&amp;characterEncoding=utf-8","root", "SDgsdgas&567Ig");
        DaoMapper.buildMapper(new MySqlJDBCDaoController());
    }

    @Test
    public void duplicateReceptionTest() throws PersistException, RowNotUniqueException{
//
//        MySqlJDBCDaoController daoController = new MySqlJDBCDaoController();
//
//        ReceptionDAO dao = DaoMapper.getMapper().getDao(ReceptionDAO.class);
//
//        Reception rec = dao.getByPK(2, connection);
//
//        rec.setStatus(Reception.Status.CANCELED);
//
//        dao.update(rec, connection);
//
//        System.gc();

//        dao.getByPK(2, connection);


//        ServiceMapper.getMapper().getService(ReviewService.class).sendReviews();

        ReviewDAO dao = DaoMapper.getMapper().getDao(ReviewDAO.class);

        try {
            connection.setAutoCommit(false);

            List<Review> reviews = dao.getUnsentReviews(connection);
            // TODO просто заглушка
            reviews.forEach(r -> dao.changeStatus(r, Review.Status.SENT, connection));

            dao.getReviewByToken("cc87d4b7149ebc79df5069326358724c", connection);

            connection.setAutoCommit(true);
        }catch (SQLException e){

        }




//        ImmutableReception immutableReception1 =  ImmutableReception.of(reception1);
//
//        Reception reception2 = dao.getByPK(2, connection);
//        ImmutableReception immutableReception2 =  ImmutableReception.of(reception2);
//
//        immutableReception2 = immutableReception2.setTime(LocalDateTimeFormatter.toSqlTime(TimePlanning.startOfDay(LocalDate.now())));
//
//        dao.save(immutableReception1.getReception(), connection);
//        dao.save(immutableReception2.getReception(), connection);

//
//        MasterDAO masterDAO = new MasterDAO(daoController);
//        ServiceDAO serviceDAO = new ServiceDAO(daoController);
//        MastersServiceDAO mastersServiceDAO = new MastersServiceDAO(daoController);
//
//        Master master = new Master();
//        master.setName("Инна");
//        master.setSurname("Иванова");
//
//        master.setUser(1);
//
//        masterDAO.save(master, connection);
//
//
//        Service service = new Service();
//        service.setName("Макияж");
//
//        serviceDAO.save(service, connection);
//
//        MastersService mastersService = new MastersService();
//        mastersService.setMaster(master);
//        mastersService.setService(service);
//
//        mastersServiceDAO.save(mastersService, connection);
    }




    @After
    public void closeConnection() throws SQLException{
        connection.close();
    }

}
