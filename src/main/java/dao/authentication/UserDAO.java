package dao.authentication;

import dao.GenericDAO;
import entity.authentication.User;
import myPersistenceSystem.JDBCDaoController;
import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;
import myPersistenceSystem.criteria.CriteriaBuilder;
import myPersistenceSystem.criteria.predicates.PredicateBuilder;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Implement DAO of User entity.
 */
public class UserDAO implements GenericDAO<User, Integer> {

    private JDBCDaoController controller;
    private Class<User> clazz;

    public UserDAO(JDBCDaoController controller) {
        this.controller = controller;
        clazz = User.class;
    }

    @Override
    public User getByPK(Integer key, Connection connection) throws PersistException {
        return controller.getByPK(key, clazz, connection);
    }

    @Override
    public void update(User object, Connection connection) throws PersistException {
        controller.update(object, connection);
    }

    @Override
    public void delete(User object, Connection connection) throws PersistException {
        controller.delete(object, connection);
    }

    @Override
    public List<User> getALL(Connection connection) throws PersistException {
        return controller.getALL(clazz, connection);
    }

    @Override
    public void save(User object, Connection connection) throws PersistException, RowNotUniqueException {
        controller.save(object, connection);
    }

    public Optional<User> getByEmail(Object value, Connection connection) throws PersistException {

        CriteriaBuilder<User> criteriaBuilder = controller.getCriteriaBuilder(clazz);
        PredicateBuilder<User> predicateBuilder = criteriaBuilder.getPredicateBuilder(clazz);

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.equal("email", value)
        );

        List<User> list = controller.getByCriteria(clazz, criteriaBuilder, connection);
        if (list.isEmpty()){
            return Optional.empty();
        }else if (list.size() > 1){
            throw new PersistException(String.format("More than one User with email '%s'", value));
        }

        return Optional.of(list.get(0));
    }
}
