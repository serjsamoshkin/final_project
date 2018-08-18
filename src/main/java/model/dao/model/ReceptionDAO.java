package model.dao.model;

import model.dao.GenericDAO;
import model.entity.model.Master;
import model.entity.model.Reception;
import persistenceSystem.JDBCDaoController;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import persistenceSystem.criteria.CriteriaBuilder;
import persistenceSystem.criteria.predicates.PredicateBuilder;
import util.LocalDateTimeFormatter;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Implement DAO of Reception model.entity.
 *
 * {@inheritDoc}
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

        java.sql.Time sqlStartTime = LocalDateTimeFormatter.toSqlTime(startTime);
        java.sql.Time sqlEndTime = LocalDateTimeFormatter.toSqlTime(endTime);

        /*
        Generates a query like:
        SELECT * FROM beauty_saloon.receptions
        WHERE
            (reception_id <> 8  AND masters_master_id = 1  AND reception_day = '2018-08-18'
                AND (
                        (reception_time < '10:00:00'  AND reception_end_time > '10:00:00' ) OR (reception_time < '12:00:00'  AND reception_end_time > '12:00:00' )
                        OR reception_time = '10:00:00'
                    )
            );
         */
        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.notEqual("id", recId.orElse(-1)),
                predicateBuilder.equal("master", master.getId()),
                predicateBuilder.equal("day", LocalDateTimeFormatter.toSqlDate(date)),
                criteriaBuilder.Or(
                        criteriaBuilder.And(
                                predicateBuilder.less("time", sqlStartTime),
                                predicateBuilder.greater("endTime", sqlStartTime)),
                        criteriaBuilder.And(
                                predicateBuilder.less("time", sqlEndTime),
                                predicateBuilder.greater("endTime", sqlEndTime)),
                        predicateBuilder.equal("time", sqlStartTime)
                )
        );

        List<Reception> list = controller.getByCriteria(Reception.class, criteriaBuilder, connection);

        return !list.isEmpty();

    }


}
