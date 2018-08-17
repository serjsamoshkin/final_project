package dao.model;

import dao.GenericDAO;
import entity.authentication.User;
import entity.model.Master;
import entity.model.Reception;
import entity.model.Service;
import myPersistenceSystem.JDBCDaoController;
import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;
import myPersistenceSystem.criteria.CriteriaBuilder;
import myPersistenceSystem.criteria.predicates.PredicateBuilder;
import util.LocalDateTimeFormatter;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Implement DAO of Reception entity.
 *
 * * {@inheritDoc}
 */
public class ReceptionDAO implements GenericDAO<Reception, Integer> {

    private JDBCDaoController controller;
    private Class<Reception> clazz;{
        clazz = Reception.class;
    }

    public ReceptionDAO(JDBCDaoController controller) {
        this.controller = controller;
    }

    @Override
    public Reception getByPK(Integer key, Connection connection) throws PersistException {
        return controller.getByPK(key, clazz, connection);
    }

    @Override
    public void update(Reception object, Connection connection) throws PersistException {
        controller.update(object, connection);
    }

    @Override
    public void delete(Reception object, Connection connection) throws PersistException {
        controller.delete(object, connection);
    }

    @Override
    public List<Reception> getALL(Connection connection) throws PersistException {
        return controller.getALL(clazz, connection);
    }

    @Override
    public void save(Reception object, Connection connection) throws PersistException, RowNotUniqueException {
        controller.save(object, connection);
    }

    public boolean checkReservationInSchedule(LocalDate date, LocalTime startTime, LocalTime endTime,
                                              Master master, Optional<Integer> recId, Connection connection){

        CriteriaBuilder<Reception> criteriaBuilder = controller.getCriteriaBuilder(Reception.class);
        PredicateBuilder<Reception> predicateBuilder = criteriaBuilder.getPredicateBuilder(Reception.class);

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.equal("id", recId.orElse(-1)),
                predicateBuilder.equal("master", master.getId()),
                predicateBuilder.equal("day", LocalDateTimeFormatter.toSqlDate(date)),
                predicateBuilder.greaterEqual("time", LocalDateTimeFormatter.toSqlTime(startTime)),
                predicateBuilder.less("time", LocalDateTimeFormatter.toSqlTime(endTime))
        );

        List<Reception> list = controller.getByCriteria(Reception.class, criteriaBuilder, connection);

        return list.isEmpty();

    }


}
