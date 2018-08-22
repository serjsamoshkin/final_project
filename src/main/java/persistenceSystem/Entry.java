package persistenceSystem;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class Entry<T, PK> {
    private final Class<T> clazz;
    private final WeakReference<T> obj;
    private final PK key;
    private volatile EntryStatus status;{
        status = EntryStatus.ISNULL;
    }

    Entry(T obj, PK key, Class<T> clazz) {
        this.obj = new WeakReference<>(obj);
        this.key = key;
        this.clazz = clazz;
    }

    static<T, PK> Entry<T, PK> forSearch(PK key, Class<T> clazz){
        return new Entry<>(null, key, clazz);
    }



    public T getObj() {
        return obj.get();
    }

    public PK getKey() {
        return key;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public EntryStatus getStatus() {
        synchronized(clazz) {
            return status;
        }
    }

    public synchronized void setStatus(EntryStatus status) {
        synchronized(clazz) {
            this.status = status;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entry)) return false;
        Entry<?, ?> entry = (Entry<?, ?>) o;
        return Objects.equals(getClazz(), entry.getClazz()) &&
                Objects.equals(getKey(), entry.getKey());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getClazz(), getKey());
    }
}
