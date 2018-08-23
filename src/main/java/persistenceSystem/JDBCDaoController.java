package persistenceSystem;

import persistenceSystem.criteria.CriteriaBuilder;

import java.lang.ref.WeakReference;
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
     * Double binding WeekHashMap. controlMap contains WeakReference on Entity object and locks clearing in
     * {@code Map<Entry, WeakReference<Entry>>} (that is value of entryMap). Entry contains WeakReference on
     *  Entity object, thus gc can clear all values in chain of week object -> entry (-> week object) -> week entry -> week entry
     *  when reference on Entity object will be cleared.
     *
     */
    private static Map<Class, Map<Entry, WeakReference<Entry>>> entryMap;
    private static Map<Object, Entry> controlMap;

    static {
        entryMap = new ConcurrentHashMap<>();
        controlMap = new WeakHashMap<>();
    }


    @SuppressWarnings("unchecked")
    protected final <T, PK> Optional<Entry<T, PK>> getEntryByPK(final Class<T> clazz, PK key) {
        entryMap.putIfAbsent(clazz, Collections.synchronizedMap(new WeakHashMap<>()));

        return Optional.ofNullable(entryMap.get(clazz).getOrDefault(Entry.forSearch(key, clazz), new WeakReference<>(null)).get());
//        synchronized (clazz) {
//            return Optional.ofNullable(entryMap.get(clazz).values().stream().filter(e -> e.getKey().equals(key)).findFirst().orElse(null));
//        }
    }

    @SuppressWarnings("unchecked")
    protected final <T, PK> Entry<T, PK> createAndGetEntry(final Class<T> clazz, T obj, PK key) {
        entryMap.putIfAbsent(clazz, Collections.synchronizedMap(new WeakHashMap<>()));

        Entry entry = new Entry<>(obj, key, clazz);
        entryMap.get(clazz).putIfAbsent(entry, new WeakReference<>(entry));
        entry = entryMap.get(clazz).get(entry).get();
        if (entry.getObj() == null){
            synchronized (clazz){
                entry = new Entry<>(obj, key, clazz);
                entryMap.get(clazz).put(entry, new WeakReference<>(entry));
                controlMap.putIfAbsent(obj, entry);
            }
        }

//        Map<Object, Entry> valMap = entryMap.merge(clazz, Map.of(), (o, n) -> {
//            o.put(obj, o.values().stream().filter(e -> e.getKey().equals(key)).findFirst().orElse(new Entry<>(obj, key)));
//            return o;
//        });
//        return valMap.get(obj);
        return entry;
    }

    protected final <T, PK> void clearEntry(final Class<T> clazz, PK key){
        entryMap.get(clazz).remove(Entry.forSearch(key, clazz));
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
