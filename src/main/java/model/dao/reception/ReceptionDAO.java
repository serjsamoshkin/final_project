package model.dao.reception;

import model.dao.GenericDAO;

import model.entity.authentication.User;
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
import util.properties.PaginationPropertiesReader;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
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
    public void update(Reception object, Connection connection) throws PersistException, ConcurrentModificationException {
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
                    predicateBuilder.equal("day", LocalDateTimeFormatter.toSqlDate(date)),
                    predicateBuilder.notEqual("status", Reception.Status.CANCELED)
            );
        }else {
            criteriaBuilder = criteriaBuilder.And(
                    predicateBuilder.equal("day", LocalDateTimeFormatter.toSqlDate(date)),
                    predicateBuilder.in("master", masterList.stream().map(Master::getId).collect(Collectors.toList())),
                    predicateBuilder.notEqual("status", Reception.Status.CANCELED)
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
            (reception_id <> 8  AND masters_master_id = 1  AND reception_day = '2018-08-18' AND reception_status <> 'CANCELED'
                AND ((reception_time < '10:00:00'  AND reception_end_time > '10:00:00' )
                    OR  (reception_time < '12:00:00'  AND reception_end_time > '12:00:00' )
                    OR  reception_time = '10:00:00')
            );
         */
        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.notEqual("id", recId.orElse(-1)),
                predicateBuilder.equal("master", master.getId()),
                predicateBuilder.equal("day", LocalDateTimeFormatter.toSqlDate(date)),
                predicateBuilder.notEqual("status", Reception.Status.CANCELED),
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


        //language=MySQL
//        String query =
//                "CREATE TEMPORARY TABLE tmp\n" +
//                "SELECT services.*, MIN(DATE_ADD(receptions.reception_time, INTERVAL - services.service_duration * ? MINUTE)) AS min_time\n" +
//                "FROM\n" +
//                "    beauty_saloon.services\n" +
//                "        JOIN beauty_saloon.masters_services ON services.service_id = masters_services.services_service_id\n" +
//                "        JOIN beauty_saloon.masters ON masters_services.masters_master_id = masters.master_id\n" +
//                "        LEFT JOIN beauty_saloon.receptions ON masters.master_id = receptions.masters_master_id " +
//                "           AND receptions.reception_day = ? AND receptions.reception_time > ? " +
//                "           AND receptions.reception_status <> 'CANCELED'\n" +
//                "WHERE\n" +
//                "    masters.master_id = ? \n" +
//                "GROUP BY services.service_id;\n" +
//
//                "SELECT tmp.* \n" +
//                "FROM tmp \n" +
//                "WHERE min_time IS NULL OR min_time >= ?";

        /*
        SELECT t.*
        FROM (
            SELECT services.*, MIN(DATE_ADD(receptions.reception_time, INTERVAL - services.service_duration * 60 MINUTE)) AS min_time
        FROM
            beauty_saloon.services
                JOIN beauty_saloon.masters_services ON services.service_id = masters_services.services_service_id
                JOIN beauty_saloon.masters ON masters_services.masters_master_id = masters.master_id
                LEFT JOIN beauty_saloon.receptions ON masters.master_id = receptions.masters_master_id
                    AND receptions.reception_time > '12:00:00' AND receptions.reception_day = '2018-08-19'
                    AND receptions.reception_status <> 'CANCELED'
        WHERE
            masters.master_id = '1'
        GROUP BY services.service_id
        ) AS t
        WHERE min_time >= '12:00:00' OR min_time is null
     */

        //language=MySQL
        String query = "SELECT t.* \n" +
                        "FROM (\n" +
                        "   SELECT services.*, MIN(DATE_ADD(receptions.reception_time, INTERVAL - services.service_duration * ? MINUTE)) AS min_time\n" +
                        "   FROM\n" +
                        "       services\n" +
                        "        JOIN masters_services ON services.service_id = masters_services.services_service_id\n" +
                        "        JOIN masters ON masters_services.masters_master_id = masters.master_id\n" +
                        "        LEFT JOIN receptions ON receptions.reception_day = ? \n" +
                        "           AND receptions.reception_time >= ? AND receptions.reception_STATUS <> 'CANCELED'\n" +
                        "   WHERE\n" +
                        "   masters.master_id = ? \n" +
                        "   GROUP BY services.service_id\n" +
                        ") AS t\n" +
                        "WHERE min_time is null OR min_time >= ?";

        CriteriaBuilder<Service> criteriaBuilder = controller.getCriteriaBuilder(Service.class);
        criteriaBuilder = criteriaBuilder.rowQuery(query,
                List.of(TimePlanning.getTimeModulator(),
                        LocalDateTimeFormatter.toSqlDate(date),
                        LocalDateTimeFormatter.toSqlTime(time),
                        master.getId(),
                        LocalDateTimeFormatter.toSqlTime(time)));

        List<Service> list =  controller.getByCriteria(Service.class, criteriaBuilder, connection);
        return new HashSet<>(list);

    }

    public List<Reception> getUserReceptions(User user, Connection connection, int page){

        CriteriaBuilder<Reception> criteriaBuilder = controller.getCriteriaBuilder(Reception.class);
        PredicateBuilder<Reception> predicateBuilder = criteriaBuilder.getPredicateBuilder(Reception.class);

         /*
        Generates query like:
        SELECT
            *
        FROM
            beauty_saloon.receptions
        WHERE
            users_user_id = '1';
         */

        int rowsForPage = Integer.valueOf(PaginationPropertiesReader.getInstance()
                .getPropertyValue("user_reception_count"));

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.equal("user", user.getId())
        );

        criteriaBuilder.orderBy(Reception.class, "day", CriteriaBuilder.Order.ASC);
        criteriaBuilder.orderBy(Reception.class, "time", CriteriaBuilder.Order.ASC);
        criteriaBuilder.limit(rowsForPage*(page-1), rowsForPage*page);

        return controller.getByCriteria(Reception.class, criteriaBuilder, connection);

    }


    public List<Reception> getReceptionsWithLimitOrderedBy(Connection connection, int page, String sortField, CriteriaBuilder.Order order){

        CriteriaBuilder<Reception> criteriaBuilder = controller.getCriteriaBuilder(Reception.class);

       /*
        Generates query like:
        SELECT
            *
        FROM
            beauty_saloon.receptions
         */

        int rowsForPage = Integer.valueOf(PaginationPropertiesReader.getInstance()
                .getPropertyValue("admin_reception_count"));


        criteriaBuilder.orderBy(Reception.class, sortField, order);
        if (!sortField.equals("time")) {
            criteriaBuilder.orderBy(Reception.class, "time", CriteriaBuilder.Order.ASC);
        }
        criteriaBuilder.limit(rowsForPage*(page-1), rowsForPage*page);

        return controller.getByCriteria(Reception.class, criteriaBuilder, connection);

    }


    public int getUserReceptionsCount(User user, Connection connection){

        //language=MySQL
        String query =
                "SELECT count(*)\n" +
                        "FROM receptions receptions\n" +
                        "WHERE receptions.users_user_id = ? ";

        int count = 0;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, user.getId());
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                count = rs.getInt(1);
            }
        }catch (SQLException e){
            throw new PersistException(e);
        }

        return count;

    }


    public int getReceptionsCount(Connection connection){

        //language=MySQL
        String query = "SELECT count(*)\n" +
                        "FROM receptions receptions";

        int count = 0;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                count = rs.getInt(1);
            }
        }catch (SQLException e){
            throw new PersistException(e);
        }

        return count;

    }

}
