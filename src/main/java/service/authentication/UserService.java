package service.authentication;

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
import service.AbstractService;
import service.ServiceMapper;

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

    public UserService(ServletContext context, DataSource dataSource) {
        super(context, dataSource);

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
                    findUser = Optional.of(user.get());
                }
            }
        }
        catch (SQLException e){
            throw new PersistException(e);
        }

        return findUser;
    }


    public List<Map<String, Object>> getWrappedUserList(int page) throws PersistException{

        // TODO пэджинация

        DataSource ds = getDataSource();
        UserDAO userDao = DaoMapper.getMapper().getDao(UserDAO.class);

        List<User> users;

        try (Connection connection = ds.getConnection()){
            users = userDao.getALL(connection);
        }catch (SQLException e){
            throw new PersistException(e);
        }

        List<Map<String, Object>> userList = new ArrayList<>();

        for (User user :
                users) {
            userList.add(getWrappedUser(user));
        }

        return userList;

    }

    // TODO перейти на Optional
    public User getDefUser(){
        return User.NOT_AUTHENTICATED;
    }

    public Map<String, Object> getWrappedDefUser(){
        return getWrappedUser(User.NOT_AUTHENTICATED);
    }

    /**
     * Method adds user's public fields to Map key and adds additional keys like isAdmin to be read in jsp page
     *  Also method wraps {@code User} object in "obj" key of returning Map.
     *
     * Returning Map is served to set view in JSP with EL and JSTL-tags functions.
     *
     * @param user to wrap
     * @return {@code HashMap<String, Object>}
     */
    public Map<String, Object> getWrappedUser(User user){

        RoleService roleService = ServiceMapper.getMapper().getService(RoleService.class);

        Role admin = roleService.getRoleAdministrator();
        Role master = ServiceMapper.getMapper().getService(RoleService.class).getRoleMaster();


        Map<String, Object> userMap = new HashMap<>();
        userMap.put("obj", user);

        userMap.put("isAuthorized", user != User.NOT_AUTHENTICATED);
        userMap.put("isAdmin", user.getRoles().contains(admin));
        userMap.put("isMaster", user.getRoles().contains(master));
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());

        Set<Role> roles = user.getRoles();
        userMap.put("rolesList", roles);
        StringBuilder str = new StringBuilder();
        for (Role role:
             roles) {
            str.append(role.getName());
            str.append("/");
        }
        if (str.length() > 0) {
            str.deleteCharAt(str.length() - 1);
        }
        userMap.put("rolesStr", str.toString());

        return userMap;

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
