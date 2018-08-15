package dao.model;

import dao.GenericDAO;
import entity.model.Master;
import entity.model.MastersService;
import entity.model.Service;
import myPersistenceSystem.JDBCDaoController;
import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;
import myPersistenceSystem.criteria.CriteriaBuilder;
import myPersistenceSystem.criteria.predicates.PredicateBuilder;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implement DAO of Master entity.
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

//        CriteriaBuilder<MastersService> criteriaBuilder = controller.getCriteriaBuilder(MastersService.class);
//        PredicateBuilder<MastersService> predicateBuilder = criteriaBuilder.getPredicateBuilder(MastersService.class);
//
//        criteriaBuilder = criteriaBuilder.And(
//                predicateBuilder.Equal("service", service.getId())
//        );
//
//        List<MastersService> list = controller.getByCriteria(MastersService.class, criteriaBuilder, connection);
//        if (list.isEmpty()) {
//            return new HashSet<>();
//        }
//
//        Set<Master> masters = new HashSet<>();
//        for (MastersService i : list) {
//            masters.add(i.getMaster());
//        }
//        return masters;

        CriteriaBuilder<Master> criteriaBuilder = controller.getCriteriaBuilder(Master.class);
        PredicateBuilder<Service> predicateBuilder = criteriaBuilder.getPredicateBuilder(Service.class);

        criteriaBuilder = criteriaBuilder.addJoin(Master.class, "id", MastersService.class, "master");
        criteriaBuilder = criteriaBuilder.addJoin(MastersService.class, "service", Service.class, "id");

        criteriaBuilder = criteriaBuilder.And(
                predicateBuilder.Equal("name", service.getName())
        );

        List<Master> list = controller.getByCriteria(Master.class, criteriaBuilder, connection);

        return list;

    }

}
