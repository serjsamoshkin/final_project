package model.dao.reception;

import model.dao.GenericDAO;
import model.entity.reception.Master;
import model.entity.reception.Reception;
import model.entity.reception.Service;
import persistenceSystem.JDBCDaoController;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import persistenceSystem.criteria.CriteriaBuilder;
import persistenceSystem.criteria.predicates.PredicateBuilder;
import util.datetime.LocalDateTimeFormatter;
import util.datetime.TimePlanning;

import javax.servlet.ServletResponse;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<Reception> getMastersReceptions(LocalDate date, List<Master> masterList, Connection connection) {
        CriteriaBuilder<Reception> criteriaBuilder = controller.getCriteriaBuilder(Reception.class);
        PredicateBuilder<Reception> predicateBuilder = criteriaBuilder.getPredicateBuilder(Reception.class);

        if (masterList.isEmpty()) {
            criteriaBuilder = criteriaBuilder.And(
                    predicateBuilder.equal("day", LocalDateTimeFormatter.toSqlDate(date))
            );
        }else {
            criteriaBuilder = criteriaBuilder.And(
                    predicateBuilder.equal("day", LocalDateTimeFormatter.toSqlDate(date)),
                    predicateBuilder.in("master", masterList.stream().map(Master::getId).collect(Collectors.toList()))
            );
        }

        return controller.getByCriteria(Reception.class, criteriaBuilder, connection);

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
    public Set<Service> getServiceListForTimeAndMaster(LocalDate date, LocalTime time, Master master, Connection connection) {

        /*
        SELECT t.*
        FROM (
            SELECT services.*, MIN(DATE_ADD(receptions.reception_time, INTERVAL - services.service_duration * 60 MINUTE)) AS min_time
        FROM
            beauty_saloon.services
                JOIN beauty_saloon.masters_services ON services.service_id = masters_services.services_service_id
                JOIN beauty_saloon.masters ON masters_services.masters_master_id = masters.master_id
                LEFT JOIN beauty_saloon.receptions ON masters.master_id = receptions.masters_master_id
        WHERE
            masters.master_id = '1' AND ((receptions.reception_time > '12:00:00' AND receptions.reception_day = '2018-08-19')
            OR receptions.reception_time IS NULL)
        GROUP BY services.service_id
        ) AS t
        WHERE min_time >= '12:00:00' OR min_time is null
     */

        //language=MySQL
        String query =
                "SELECT t.* \n" +
                "FROM (\n" +
                "    SELECT services.*, MIN(DATE_ADD(receptions.reception_time, INTERVAL - services.service_duration * ? MINUTE)) AS min_time\n" +
                "FROM\n" +
                "    beauty_saloon.services\n" +
                "        JOIN beauty_saloon.masters_services ON services.service_id = masters_services.services_service_id\n" +
                "        JOIN beauty_saloon.masters ON masters_services.masters_master_id = masters.master_id\n" +
                "        LEFT JOIN beauty_saloon.receptions ON masters.master_id = receptions.masters_master_id\n" +
                "WHERE\n" +
                "    masters.master_id = ? AND ((receptions.reception_day = ? AND receptions.reception_time > ?)  \n" +
                "    OR receptions.reception_time IS NULL)\n" +
                "GROUP BY services.service_id\n" +
                ") AS t\n" +
                "WHERE min_time >= ? OR min_time is null";

        CriteriaBuilder<Service> criteriaBuilder = controller.getCriteriaBuilder(Service.class);
        criteriaBuilder = criteriaBuilder.rowQuery(query,
                List.of(TimePlanning.getTimeModulator(),
                        master.getId(),
                        LocalDateTimeFormatter.toSqlDate(date),
                        LocalDateTimeFormatter.toSqlTime(time),
                        LocalDateTimeFormatter.toSqlTime(time)));

        List<Service> list =  controller.getByCriteria(Service.class, criteriaBuilder, connection);
        return new HashSet<>(list);

    }



}
