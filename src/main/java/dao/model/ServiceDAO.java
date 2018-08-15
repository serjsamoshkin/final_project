package dao.model;

import dao.GenericDAO;
import entity.model.Service;
import myPersistenceSystem.JDBCDaoController;
import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;
import myPersistenceSystem.criteria.CriteriaBuilder;
import myPersistenceSystem.criteria.predicates.PredicateBuilder;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Implement DAO of Master entity.
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

    public Optional<Service> getByName(String value, Connection connection) throws PersistException {

        CriteriaBuilder<Service> criteriaBuilder = controller.getCriteriaBuilder(clazz);
        PredicateBuilder<Service> predicateBuilder = criteriaBuilder.getPredicateBuilder(clazz);

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.Equal("name", value)
        );

        List<Service> list = controller.getByCriteria(clazz, criteriaBuilder, connection);
        if (list.isEmpty()){
            return Optional.empty();
        }else if (list.size() > 1){
            throw new PersistException(String.format("More than one Service with name '%s'", value));
        }

        return Optional.of(list.get(0));
    }

}
