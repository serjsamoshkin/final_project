package model.service.util;

import model.dao.DaoMapper;
import model.dao.reception.MasterDAO;
import model.dao.reception.MastersServiceDAO;
import model.dao.reception.ServiceDAO;
import model.entity.reception.Master;
import model.entity.reception.MastersService;
import model.entity.reception.Service;
import model.service.authentication.RoleService;
import persistenceSystem.RowNotUniqueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.service.AbstractService;
import model.service.ServiceMapper;
import model.service.authentication.UserService;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataInitializerService extends AbstractService{

    private static final Logger logger = LogManager.getLogger(DataInitializerService.class);

    public DataInitializerService(DataSource dataSource) {
        super(dataSource);
    }

    public void initIfNeed(){

        MasterDAO masterDAO = DaoMapper.getMapper().getDao(MasterDAO.class);
        ServiceDAO serviceDAO = DaoMapper.getMapper().getDao(ServiceDAO.class);
        MastersServiceDAO mastersServiceDAO = DaoMapper.getMapper().getDao(MastersServiceDAO.class);

        UserService userService = ServiceMapper.getMapper().getService(UserService.class);

        try (Connection con = getDataSource().getConnection()) {

            con.setAutoCommit(false);

            Master master1 = new Master();
            master1.setUser(userService.createUser("Иваненко", "ivanenko@me.me", "1", ServiceMapper.getMapper().getService(RoleService.class).getRoleMaster()));
            master1.setName("Ann");
            master1.setSurname("Ivanenko");
            master1.setNameRu("Анна");
            master1.setSurnameRu("Иваненко");

            masterDAO.save(master1, con);

            Master master2 = new Master();
            master2.setUser(userService.createUser("Петренко", "petrenko@me.me", "1", ServiceMapper.getMapper().getService(RoleService.class).getRoleMaster()));
            master2.setName("Inna");
            master2.setSurname("Petrenko");
            master2.setNameRu("Инна");
            master2.setSurnameRu("Петренко");

            masterDAO.save(master2, con);

            Master master3 = new Master();
            master3.setUser(userService.createUser("Сидоренко", "sidorenko@me.me", "1", ServiceMapper.getMapper().getService(RoleService.class).getRoleMaster()));
            master3.setName("Daria");
            master3.setSurname("Sidorenko");
            master3.setNameRu("Дарья");
            master3.setSurnameRu("Сидоренко");

            masterDAO.save(master3, con);


            Service service1 = new Service();
            service1.setName("Makeup");
            service1.setNameRu("Макияж");
            service1.setDuration(1);
            serviceDAO.save(service1, con);

            Service service2 = new Service();
            service2.setName("Haircut");
            service2.setNameRu("Стрижка");
            service2.setDuration(2);
            serviceDAO.save(service2, con);

            Service service3 = new Service();
            service3.setName("Manicure");
            service3.setNameRu("Маникюр");
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
