package dao;

import myPersistenceSystem.PersistException;
import myPersistenceSystem.RowNotUniqueException;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

/**
 * Interface for base DAO (CRUD) operations.
 * Defines the mandatory operations to implement all life-circle operations of DAO objects.
 * @param <T> entity type
 * @param <PK> privet key type
 */
public interface GenericDAO<T, PK extends Serializable> {

    // TODO разобраться в наследовании Connection - нужно сделать ДАО коннекшенов тоже, в уроке было про это.

    public T getByPK(PK key, Connection connection) throws PersistException;

    public void update(T object, Connection connection) throws PersistException;

    public void delete(T object, Connection connection) throws PersistException;

    public List<T> getALL(Connection connection) throws PersistException;

    public void save(T object, Connection connection) throws PersistException, RowNotUniqueException;

}
