package service.initializer;

import model.dao.DaoMapper;
import model.dao.model.MasterDAO;
import model.dao.model.MastersServiceDAO;
import model.dao.model.ServiceDAO;
import model.entity.model.Master;
import model.entity.model.MastersService;
import model.entity.model.Service;
import persistenceSystem.RowNotUniqueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.AbstractService;
import service.ServiceMapper;
import service.authentication.UserService;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataInitializerService extends AbstractService{

    private static final Logger logger = LogManager.getLogger(DataInitializerService.class);

    public DataInitializerService(ServletContext context, DataSource dataSource) {
        super(context, dataSource);
    }

    public void initIfNeed(){

        MasterDAO masterDAO = DaoMapper.getMapper().getDao(MasterDAO.class);
        ServiceDAO serviceDAO = DaoMapper.getMapper().getDao(ServiceDAO.class);
        MastersServiceDAO mastersServiceDAO = DaoMapper.getMapper().getDao(MastersServiceDAO.class);

        UserService userService = ServiceMapper.getMapper().getService(UserService.class);

        try (Connection con = getDataSource().getConnection()) {

            // TODO Нет связи один к одному

            con.setAutoCommit(false);

            Master master1 = new Master();
            master1.setUser(userService.createUser("Иваненко", "ivanenko@me.me", "1"));
            master1.setName("Анна");
            master1.setSurname("Иваненко");

            masterDAO.save(master1, con);

            Master master2 = new Master();
            master2.setUser(userService.createUser("Петренко", "petrenko@me.me", "1"));
            master2.setName("Инна");
            master2.setSurname("Петренко");

            masterDAO.save(master2, con);

            Master master3 = new Master();
            master3.setUser(userService.createUser("Сидоренко", "sidorenko@me.me", "1"));
            master3.setName("Дарья");
            master3.setSurname("Сидоренко");

            masterDAO.save(master3, con);


            Service service1 = new Service();
            service1.setName("Макияж");
            service1.setDuration(2);
            serviceDAO.save(service1, con);

            Service service2 = new Service();
            service2.setName("Стрижка");
            service2.setDuration(2);
            serviceDAO.save(service2, con);

            Service service3 = new Service();
            service3.setName("Маникюр");
            service3.setDuration(2);
            serviceDAO.save(service3, con);

            MastersService mastersService1 = new MastersService();
            mastersService1.setService(service1);
            mastersService1.setMaster(master1);
            mastersServiceDAO.save(mastersService1, con);

            MastersService mastersService2 = new MastersService();
            mastersService2.setService(service2);
            mastersService2.setMaster(master1);
            mastersServiceDAO.save(mastersService2, con);

            MastersService mastersService1_1 = new MastersService();
            mastersService1_1.setService(service2);
            mastersService1_1.setMaster(master2);
            mastersServiceDAO.save(mastersService1_1, con);

            MastersService mastersService2_1 = new MastersService();
            mastersService2_1.setService(service3);
            mastersService2_1.setMaster(master2);
            mastersServiceDAO.save(mastersService2_1, con);

            MastersService mastersService3 = new MastersService();
            mastersService3.setService(service1);
            mastersService3.setMaster(master3);
            mastersServiceDAO.save(mastersService3, con);

            MastersService mastersService3_1 = new MastersService();
            mastersService3_1.setService(service3);
            mastersService3_1.setMaster(master3);
            mastersServiceDAO.save(mastersService3_1, con);

            con.commit();

        } catch (SQLException | RowNotUniqueException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

    }
}
