package myPersistenceSystem;

import myPersistenceSystem.criteria.CriteriaBuilder;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public abstract class JDBCDaoController {

    /**
     * Map<Object, Entry> - где Object представляет из себя объект(persistence object, entity). Пара хранится в WeakHashMap. Так, если сессия пользователя закончилась,
     * и сам пользователь был выгружен из сессионных параметров, сборщик может обраобтать объект и его пару Entry.
     */
    private static Map<Class, Map<Object, Entry>> entryMap;
    static {
        entryMap = new ConcurrentHashMap<>();
    }

    /**
     *
     * @param clazz
     * @param key
     * @param <T>
     * @return @return Entry<T, PK>, or null if in entryMap no obj
     */
    @SuppressWarnings("unchecked")
    protected final <T, PK> Optional<Entry<T, PK>> getEntryByObjKey(final Class<T> clazz, PK key) {
        entryMap.putIfAbsent(clazz, new ConcurrentHashMap<>());
        Map<Object, Entry> map = entryMap.get(clazz);
        Entry<T, PK> entry = null;
        for (Entry<T, PK> e:
                map.values()) {
            if  (e.getKey().equals(key)){
                entry = e;
            }
        }
        return Optional.ofNullable(entry);
    }

    @SuppressWarnings("unchecked")
    protected final <T> Optional<Entry<T, ?>> getEntryByObject(final Class<T> clazz, T obj) {
        entryMap.putIfAbsent(clazz, new WeakHashMap<>());
        return Optional.ofNullable(entryMap.get(clazz).getOrDefault(obj, null));
    }

    @SuppressWarnings("unchecked")
    protected final <T, PK> Entry<T, PK> createAndPutEntry(final Class<T> clazz, T obj, PK key) {
        Optional<Entry<T, PK>> local = getEntryByObjKey(clazz, key);
        if (!local.isPresent())
            synchronized (clazz) {
                local = getEntryByObjKey(clazz, key);
                if (!local.isPresent()) {
                    local = Optional.of(new Entry<>(obj, key));
                    entryMap.get(clazz).put(obj, local.get());
                }
            }
        return local.get();
    }

    public abstract<T, PK> T getByPK(PK key, Class<T> clazz, Connection connection) throws PersistException;

    public abstract<T> List<T> getALL(Class<T> clazz, Connection connection) throws PersistException;

    public abstract<T> void save(T object, Connection connection) throws PersistException, RowNotUniqueException;

    public abstract<T> void update(T object, Connection connection) throws PersistException;

    public abstract<T> void delete(T object, Connection connection) throws PersistException;

    public abstract<T> List<T> getByCriteria(Class<T> clazz,
                                             CriteriaBuilder<T> criteriaBuilder,
                                             Connection connection) throws PersistException;

    public abstract<T> CriteriaBuilder<T> getCriteriaBuilder(Class<T> clazz) throws PersistException;

}
