package dao.authentication;

import dao.GenericDAO;
import entity.authentication.UsersRole;
import myPersistenceSystem.JDBCDaoController;
import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;

import java.sql.Connection;
import java.util.List;

/**
 * Implement DAO of UsersRole entity.
 */
public class UserRoleDAO implements GenericDAO<UsersRole, Integer> {

    private JDBCDaoController controller;
    private Class<UsersRole> clazz;

    public UserRoleDAO(JDBCDaoController controller) {
        this.controller = controller;
        clazz = UsersRole.class;
    }

    @Override
    public UsersRole getByPK(Integer key, Connection connection) throws PersistException {
        return controller.getByPK(key, clazz, connection);
    }

    @Override
    public void update(UsersRole object, Connection connection) throws PersistException {
        controller.update(object, connection);
    }

    @Override
    public void delete(UsersRole object, Connection connection) throws PersistException {
        controller.delete(object, connection);
    }

    @Override
    public List<UsersRole> getALL(Connection connection) throws PersistException {
        return controller.getALL(clazz, connection);
    }

    @Override
    public void save(UsersRole object, Connection connection) throws PersistException, RowNotUniqueException {
        controller.save(object, connection);
    }

}
