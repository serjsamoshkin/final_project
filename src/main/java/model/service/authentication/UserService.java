package model.service.authentication;

import model.dao.DaoMapper;
import model.dao.authentication.UserDAO;
import model.dao.authentication.UserRoleDAO;
import model.entity.authentication.Role;
import model.entity.authentication.User;
import model.entity.authentication.UsersRole;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import model.service.AbstractService;
import model.service.ServiceMapper;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

// TODO пронаследоваться от абстрактного класса ContextService - у него должен быть контекст, и гет/сет
public class UserService extends AbstractService{

    private static final Logger logger = LogManager.getLogger(UserService.class);

    /**
     *  static hidden salt for all passwords
     */
    private static final String SALT = "*jjd*71Ujd";

    public UserService(DataSource dataSource) {
        super(dataSource);

        try(Connection connection = getDataSource().getConnection()) {
            if (!DaoMapper.getMapper().getDao(UserDAO.class).getByEmail("admin@me.me", connection).isPresent()){
                createUser("Administrator", "admin@me.me", "1", ServiceMapper.getMapper().getService(RoleService.class).getRoleAdministrator());
            }
        } catch (RowNotUniqueException|SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method create new {@code User} encrypts the password with BCrypt algorithm
     * and puts it in new {@code User} object.
     * {@code User} is saved in DB.
     *
     * @param name - user's name
     * @param email user's email
     * @param password user's password
     * @return new {@code User} that is already stored in the database.
     * @throws PersistException
     */
    public User createUser(String name,
                           String email,
                           String password,
                           Role... roles) throws PersistException, RowNotUniqueException {


        User user;
        try (Connection connection = getDataSource().getConnection()){

            UserDAO userDao = DaoMapper.getMapper().getDao(UserDAO.class);
            UserRoleDAO userRoleDao = DaoMapper.getMapper().getDao(UserRoleDAO.class);

            Role roleUser = ServiceMapper.getMapper().getService(RoleService.class).getRoleUser();

            // TODO передалать все на билдеры
            user = new User();
            user.setName(name);
            user.setEmail(email);

            user.setPasswordHash(BCrypt.hashpw(password + SALT, BCrypt.gensalt()));

            UsersRole usersRole = new UsersRole();
            usersRole.setUser(user);
            usersRole.setRole(roleUser);

            try {
                connection.setAutoCommit(false);

                userDao.save(user, connection);
                userRoleDao.save(usersRole, connection);

                for (Role role : roles) {
                    usersRole = new UsersRole();
                    usersRole.setUser(user);
                    usersRole.setRole(role);
                    userRoleDao.save(usersRole, connection);
                }

                connection.commit();
                connection.setAutoCommit(true);
            }catch (SQLException e){
                logger.error("Exception when creating a user", e);
                throw new PersistException(e);
            }catch (RowNotUniqueException e){
                try {
                    userRoleDao.delete(usersRole, connection);
                }catch (PersistException peEx){
                    logger.error(peEx);
                }
                try {
                    userDao.delete(user, connection);
                }catch (PersistException peEx){
                    logger.error(peEx);
                }

                throw e;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return user;

    }

    /**
     * Find user by email and check DB hash_password with entered password
     *
     * @param email user's email
     * @param password not hashed password
     * @return {@code User} Optional.empty() or authenticated {@code Optional<User>}
     * @throws PersistException Runtime exception. Can be caught if it need.
     */
    public Optional<User> getAuthenticatedUser(String email, String password) throws PersistException{
        DataSource ds = getDataSource();
        UserDAO userDao = DaoMapper.getMapper().getDao(UserDAO.class);

        Optional<User> findUser = Optional.empty();

        try ( Connection connection = ds.getConnection()) {
            Optional<User> user = userDao.getByEmail(email, connection);
            if (user.isPresent()) {
                if (BCrypt.checkpw(password + SALT, user.get().getPasswordHash())) {
                    findUser = user;
                }
            }
        }
        catch (SQLException e){
            throw new PersistException(e);
        }

        return findUser;
    }



    public Optional<User> getUserByEmail(String email) {

        Optional<User> user;
        try(Connection connection = getDataSource().getConnection()) {
            user =  DaoMapper.getMapper().getDao(UserDAO.class).getByEmail(email, connection);
        }catch (SQLException e){
            throw new PersistException(e);
        }

        return user;
    }



}
