package model.service.authentication;

import model.dao.DaoMapper;
import model.dao.authentication.RoleDAO;
import model.entity.authentication.Role;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import model.service.AbstractService;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class RoleService extends AbstractService{
    private static final Logger logger = LogManager.getLogger(RoleService.class);

    private final Role administrator;
    private final Role master;
    private final Role user;

    public RoleService(DataSource dataSource) {
        super(dataSource);

        try (Connection connection = dataSource.getConnection()) {
            administrator = DaoMapper.getMapper().getDao(RoleDAO.class).getByName("Administrator", connection).orElseGet(() -> createRole("Administrator"));
            master = DaoMapper.getMapper().getDao(RoleDAO.class).getByName("Master", connection).orElseGet(() -> createRole("Master"));
            user = DaoMapper.getMapper().getDao(RoleDAO.class).getByName("User", connection).orElseGet(() -> createRole("User"));
        }catch (SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public Role getRoleAdministrator() {
        return administrator;
    }

    public Role getRoleMaster() {
        return master;
    }

    public Role getRoleUser() {
        return user;
    }

    private static Role getEmptyRole(){

        try{
            throw new Exception();
        }catch (Exception e){
            logger.error("Request for an empty role.", e);
        }

        return Role.EMPTY_ROLE;
    }

    private Role createRole(String roleName) throws PersistException{

        try( Connection connection = getDataSource().getConnection()) {
            Role role = new Role();
            role.setName(roleName);

            try {
                DaoMapper.getMapper().getDao(RoleDAO.class).save(role, connection);
            }catch (RowNotUniqueException e){
                logger.error("Can't save new Role in DB. Role name: " + roleName);
                return getEmptyRole();
            }

            return role;
        }catch (SQLException e){
            throw new PersistException(e);
        }
    }

}
