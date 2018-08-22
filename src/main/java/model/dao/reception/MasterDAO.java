package model.dao.reception;

import model.dao.GenericDAO;
import model.entity.authentication.User;
import model.entity.reception.Master;
import model.entity.reception.MastersService;
import model.entity.reception.Service;
import persistenceSystem.JDBCDaoController;
import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;
import persistenceSystem.criteria.CriteriaBuilder;
import persistenceSystem.criteria.predicates.PredicateBuilder;

import java.sql.Connection;
import java.util.*;

/**
 * Implement DAO of Master model.entity.
 *
 * * {@inheritDoc}
 */
public class MasterDAO implements GenericDAO<Master, Integer> {

    private JDBCDaoController controller;
    private Class<Master> clazz;

    public MasterDAO(JDBCDaoController controller) {
        this.controller = controller;
        clazz = Master.class;
    }

    @Override
    public Master getByPK(Integer key, Connection connection) throws PersistException {
        return controller.getByPK(key, clazz, connection);
    }

    @Override
    public void update(Master object, Connection connection) throws PersistException {
        controller.update(object, connection);
    }

    @Override
    public void delete(Master object, Connection connection) throws PersistException {
        controller.delete(object, connection);
    }

    @Override
    public List<Master> getALL(Connection connection) throws PersistException {
        return controller.getALL(clazz, connection);
    }

    @Override
    public void save(Master object, Connection connection) throws PersistException, RowNotUniqueException {
        controller.save(object, connection);
    }

    public List<Master> getMasterListByService(Service service, Connection connection){

        if (service == Service.EMPTY_SERVICE){
            return getALL(connection);
        }

        CriteriaBuilder<Master> criteriaBuilder = controller.getCriteriaBuilder(Master.class);
        PredicateBuilder<Service> predicateBuilder = criteriaBuilder.getPredicateBuilder(Service.class);

        criteriaBuilder = criteriaBuilder.addJoin(Master.class, "id", MastersService.class, "master");
        criteriaBuilder = criteriaBuilder.addJoin(MastersService.class, "service", Service.class, "id");

        /*
        Generates query like:
        SELECT
            *
        FROM
            beauty_saloon.masters
                JOIN
            beauty_saloon.masters_services ON masters.master_id = masters_services.masters_master_id
                JOIN
            beauty_saloon.services ON masters_services.services_service_id = services.service_id
        WHERE
            service_id = '1';
         */

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.equal("id", service.getId())
        );

        List<Master> list = controller.getByCriteria(Master.class, criteriaBuilder, connection);

        return list;

    }

    public Master getMasterByUserId(int userId, Connection connection){
        CriteriaBuilder<Master> criteriaBuilder = controller.getCriteriaBuilder(Master.class);
        PredicateBuilder<Master> predicateBuilder = criteriaBuilder.getPredicateBuilder(Master.class);

        /*
        Generates query like:
        SELECT
            *
        FROM
            beauty_saloon.masters
        WHERE
            users_user_id = '1';
         */

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.equal("user", userId)
        );

        List<Master> list = controller.getByCriteria(Master.class, criteriaBuilder, connection);

        if (list.isEmpty()){
            return null;
        }

        return list.get(0);
    }

}
