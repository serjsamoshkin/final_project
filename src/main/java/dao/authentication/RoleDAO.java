package dao.authentication;

import dao.GenericDAO;
import entity.authentication.Role;
import myPersistenceSystem.JDBCDaoController;
import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;
import myPersistenceSystem.criteria.CriteriaBuilder;
import myPersistenceSystem.criteria.predicates.PredicateBuilder;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Implement DAO of Role entity.
 *
 * {@inheritDoc}
 */
public class RoleDAO implements GenericDAO<Role, Integer> {

    private JDBCDaoController controller;
    private Class<Role> clazz;

    public RoleDAO(JDBCDaoController controller) {
        this.controller = controller;
        clazz = Role.class;
    }

    @Override
    public Role getByPK(Integer key, Connection connection) throws PersistException {
        return controller.getByPK(key, clazz, connection);
    }

    @Override
    public void update(Role object, Connection connection) throws PersistException {
        controller.update(object, connection);
    }

    @Override
    public void delete(Role object, Connection connection) throws PersistException {
        controller.delete(object, connection);
    }

    @Override
    public List<Role> getALL(Connection connection) throws PersistException {
        return controller.getALL(clazz, connection);
    }

    @Override
    public void save(Role object, Connection connection) throws PersistException, RowNotUniqueException {
        controller.save(object, connection);
    }

    public Optional<Role> getByName(String value, Connection connection) throws PersistException {

        CriteriaBuilder<Role> criteriaBuilder = controller.getCriteriaBuilder(clazz);
        PredicateBuilder<Role> predicateBuilder = criteriaBuilder.getPredicateBuilder(clazz);

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.equal("name", value)
        );

        List<Role> list = controller.getByCriteria(clazz, criteriaBuilder, connection);
        if (list.isEmpty()){
            return Optional.empty();
        }else if (list.size() > 1){
            throw new PersistException(String.format("More than one Role with name '%s'", value));
        }

        return Optional.of(list.get(0));
    }
}
