package dao.model;

import dao.GenericDAO;
import entity.model.MastersService;
import myPersistenceSystem.JDBCDaoController;
import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;

import java.sql.Connection;
import java.util.List;

/**
 * Implement DAO of MastersService entity.
 *
 * {@inheritDoc}
 */
public class MastersServiceDAO implements GenericDAO<MastersService, Integer> {

    private JDBCDaoController controller;
    private Class<MastersService> clazz;{
        clazz = MastersService.class;
    }

    public MastersServiceDAO(JDBCDaoController controller) {
        this.controller = controller;
    }

    @Override
    public MastersService getByPK(Integer key, Connection connection) throws PersistException {
        return controller.getByPK(key, clazz, connection);
    }

    @Override
    public void update(MastersService object, Connection connection) throws PersistException {
        controller.update(object, connection);
    }

    @Override
    public void delete(MastersService object, Connection connection) throws PersistException {
        controller.delete(object, connection);
    }

    @Override
    public List<MastersService> getALL(Connection connection) throws PersistException {
        return controller.getALL(clazz, connection);
    }

    @Override
    public void save(MastersService object, Connection connection) throws PersistException, RowNotUniqueException {
        controller.save(object, connection);
    }
}
