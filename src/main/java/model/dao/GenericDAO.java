package model.dao;

import persistenceSystem.PersistException;
import persistenceSystem.RowNotUniqueException;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

/**
 * Interface for base DAO (CRUD) operations.
 * Defines the mandatory operations to implement all life-circle operations of DAO objects.
 * @param <T> model.entity type
 * @param <PK> privet key type
 */
public interface GenericDAO<T, PK extends Serializable> {

    T getByPK(PK key, Connection connection) throws PersistException;

    void update(T object, Connection connection) throws PersistException;

    void delete(T object, Connection connection) throws PersistException;

    List<T> getALL(Connection connection) throws PersistException;

    void save(T object, Connection connection) throws PersistException, RowNotUniqueException;

}
