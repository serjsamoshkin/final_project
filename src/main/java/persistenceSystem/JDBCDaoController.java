package persistenceSystem;

import persistenceSystem.criteria.CriteriaBuilder;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All public methods of JDBCDaoController subclasses throws {@link PersistException}.
 * Contains the functionality of caching persistence data for fast access to common data and to prevent infinite recursion.
 * Declares CRUD operations to be implemented in subclasses
 */
public abstract class JDBCDaoController {

    /**
     * {@code value} of the map is {@code Map<Object, Entry>},  where Object is an persistence object - model.entity.
     * The pair of the inner (value) map is stored in the WeakHashMap.
     * So, for example, if the user's session is ended and {@code User} has been unloaded from the session attributes,
     * the garbage collector can process the object ({@code User} in this example) and its Entry pair.
     */
    private static Map<Class, Map<Object, Entry>> entryMap;
    static {
        entryMap = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    protected final <T, PK> Optional<Entry<T, PK>> getEntryByPK(final Class<T> clazz, PK key) {
        entryMap.putIfAbsent(clazz, new WeakHashMap<>());
        synchronized (clazz) {
            return Optional.ofNullable(entryMap.get(clazz).values().stream().filter(e -> e.getKey().equals(key)).findFirst().orElse(null));
        }
    }

    @SuppressWarnings("unchecked")
    protected final <T, PK> Entry<T, PK> createAndGetEntry(final Class<T> clazz, T obj, PK key) {
        entryMap.putIfAbsent(clazz, new WeakHashMap<>());

        Map<Object, Entry> valMap = entryMap.merge(clazz, Map.of(), (o, n) -> {
            o.put(obj, o.values().stream().filter(e -> e.getKey().equals(key)).findFirst().orElse(new Entry<>(obj, key)));
            return o;
        });
        return valMap.get(obj);
    }


    /**
     * returns an object by PrivetKey - a field with @Id annotation is used to get the name of the SQL-table
     * id field and the value of the parameter for it.
     *
     * @param key to find row in the table with id value
     * @param clazz marker class, used to all reflection operations
     * @param connection SQLConnection
     * @param <T> generic type parameter of the object's return type
     * @param <PK> type of the PrivetKey
     * @return object of T type
     * @throws PersistException like all methods of this class
     */
    public abstract<T, PK> T getByPK(PK key, Class<T> clazz, Connection connection) throws PersistException;

    /**
     * returns list of all persistence object from SQL-table
     *
     * @param clazz marker class, used to all reflection operations
     * @param connection SQLConnection
     * @param <T> generic parameter of the object's return type
     * @return list of all persistence object of T type from SQL-table
     * @throws PersistException like all methods of this class
     */
    public abstract<T> List<T> getALL(Class<T> clazz, Connection connection) throws PersistException;

    /**
     * Save an T object in DB.
     *
     * @param object of T type to save (persist) in DB
     * @param connection SQLConnection
     * @param <T> generic type parameter of the object to save
     * @throws PersistException like all methods of this class
     * @throws RowNotUniqueException this exception will be thrown if SQL throws an exception while saving the object.
     *  Can mask all SQL exceptions. Be careful.
     */
    public abstract<T> void save(T object, Connection connection) throws PersistException, RowNotUniqueException;

    /**
     * Update values in SQL table row with id = object.getId()
     *
     * @param object of T type to update (persist) in DB
     * @param connection SQLConnection
     * @param <T> generic type parameter of the object to update
     * @throws PersistException like all methods of this class
     * @throws ConcurrentModificationException if T object is annotated with @VersionControl and there was a
     *  saving of several versions of the object with one id
     */
    public abstract<T> void update(T object, Connection connection) throws PersistException, ConcurrentModificationException;

    /**
     * Delete row in SQL table where id = object.getId()
     *
     * @param object of T type save (persist) in DB
     * @param connection SQLConnection
     * @param <T> generic type parameter of the object to delete
     * @throws PersistException like all methods of this class
     */
    public abstract<T> void delete(T object, Connection connection) throws PersistException;

    /**
     * returns {@code List<T>} of persistence objects according to the conditions described in the criteriaBuilder.
     *
     * @param clazz marker class, used to all reflection operations
     * @param criteriaBuilder contains conditions for the selection of rows from DB
     * @param connection SQLConnection
     * @param <T> generic type parameter of the objects to return
     * @return selected with the criteriaBuilder objects
     * @throws PersistException like all methods of this class
     *
     * @see CriteriaBuilder
     */
    public abstract<T> List<T> getByCriteria(Class<T> clazz,
                                             CriteriaBuilder<T> criteriaBuilder,
                                             Connection connection) throws PersistException;

    /**
     * returns new CriteriaBuilder parameterized of T type
     *
     * @param clazz marker class, used to all reflection operations
     * @param <T> generic type parameter of the CriteriaBuilder to return
     * @return {@code CriteriaBuilder}
     * @throws PersistException like all methods of this class
     *
     * @see CriteriaBuilder
     */
    public abstract<T> CriteriaBuilder<T> getCriteriaBuilder(Class<T> clazz) throws PersistException;

}
