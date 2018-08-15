package dao.model;

import entity.model.Master;
import entity.model.MastersService;
import entity.model.Service;
import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;
import myPersistenceSystem.sql.MySqlJDBCDaoController;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MasterTests {

    private static Connection connection;

    @Before
    public void setConnection() throws SQLException{
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/beauty_saloon?autoReconnect=true&useSSL=false&useUnicode=true&amp;characterEncoding=utf-8","root", "SDgsdgas&567Ig");
    }

    @Test
    public void creationalMasterTest() throws PersistException, RowNotUniqueException{

//        MySqlJDBCDaoController daoController = new MySqlJDBCDaoController();
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

}
