package model.dao.reception;

import model.dao.GenericDAO;
import model.entity.reception.*;
import persistenceSystem.JDBCDaoController;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import persistenceSystem.criteria.CriteriaBuilder;
import persistenceSystem.criteria.predicates.PredicateBuilder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implement DAO of Review model.entity.
 *
 * * {@inheritDoc}
 */
public class ReviewDAO implements GenericDAO<Review, Integer> {

    private JDBCDaoController controller;
    private Class<Review> clazz;

    public ReviewDAO(JDBCDaoController controller) {
        this.controller = controller;
        clazz = Review.class;
    }

    @Override
    public Review getByPK(Integer key, Connection connection) throws PersistException {
        return controller.getByPK(key, clazz, connection);
    }

    @Override
    public void update(Review object, Connection connection) throws PersistException {
        controller.update(object, connection);
    }

    @Override
    public void delete(Review object, Connection connection) throws PersistException {
        controller.delete(object, connection);
    }

    @Override
    public List<Review> getALL(Connection connection) throws PersistException {
        return controller.getALL(clazz, connection);
    }

    @Override
    public void save(Review object, Connection connection) throws PersistException, RowNotUniqueException {
        controller.save(object, connection);
    }


    public List<Review> getUnsentReviews(Connection connection){
        CriteriaBuilder<Review> criteriaBuilder = controller.getCriteriaBuilder(Review.class);
        PredicateBuilder<Review> predicateBuilder = criteriaBuilder.getPredicateBuilder(Review.class);

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.equal("status", Review.Status.NEW)
        );

        criteriaBuilder.setLock();

        return controller.getByCriteria(Review.class, criteriaBuilder, connection);
    }

    /**
     * For using in lambdas
     */
    public void changeStatus(Review review, Review.Status status, Connection connection){
        review.setStatus(status);
        update(review, connection);
    }

    public Optional<Review> getReviewWithLock(int id, Connection connection){
        CriteriaBuilder<Review> criteriaBuilder = controller.getCriteriaBuilder(Review.class);
        PredicateBuilder<Review> predicateBuilder = criteriaBuilder.getPredicateBuilder(Review.class);

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.equal("id", id)
        );

        criteriaBuilder.setLock();

        return Optional.ofNullable(controller.getByCriteria(Review.class, criteriaBuilder, connection).get(0));
    }

    public Optional<Review> getReviewByToken(String token, Connection connection){
        CriteriaBuilder<Review> criteriaBuilder = controller.getCriteriaBuilder(Review.class);
        PredicateBuilder<Review> predicateBuilder = criteriaBuilder.getPredicateBuilder(Review.class);

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.equal("token", token)
        );

        return Optional.ofNullable(controller.getByCriteria(Review.class, criteriaBuilder, connection).get(0));
    }

    public List<Review> getReviewsByReceptions(List<Reception> receptions, Connection connection) {

        if (receptions.isEmpty()) {
            return new ArrayList<>();
        }

        CriteriaBuilder<Review> criteriaBuilder = controller.getCriteriaBuilder(Review.class);
        PredicateBuilder<Review> predicateBuilder = criteriaBuilder.getPredicateBuilder(Review.class);


        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.in("reception", receptions.stream().map(Reception::getId).collect(Collectors.toList()))
        );

        return controller.getByCriteria(Review.class, criteriaBuilder, connection);

    }

}
