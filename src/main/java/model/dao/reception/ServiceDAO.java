package model.dao.reception;

import model.dao.GenericDAO;
import model.entity.reception.Service;
import persistenceSystem.JDBCDaoController;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import persistenceSystem.criteria.CriteriaBuilder;
import persistenceSystem.criteria.predicates.PredicateBuilder;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Implement DAO of Master model.entity.
 *
 * * {@inheritDoc}
 */
public class ServiceDAO implements GenericDAO<Service, Integer> {

    private JDBCDaoController controller;
    private Class<Service> clazz;{
        clazz = Service.class;
    }

    public ServiceDAO(JDBCDaoController controller) {
        this.controller = controller;
    }

    @Override
    public Service getByPK(Integer key, Connection connection) throws PersistException {
        return controller.getByPK(key, clazz, connection);
    }

    @Override
    public void update(Service object, Connection connection) throws PersistException {
        controller.update(object, connection);
    }

    @Override
    public void delete(Service object, Connection connection) throws PersistException {
        controller.delete(object, connection);
    }

    @Override
    public List<Service> getALL(Connection connection) throws PersistException {
        return controller.getALL(clazz, connection);
    }

    @Override
    public void save(Service object, Connection connection) throws PersistException, RowNotUniqueException {
        controller.save(object, connection);
    }


}
