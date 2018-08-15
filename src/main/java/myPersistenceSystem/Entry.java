package myPersistenceSystem;

public class Entry<T, PK> {
    private final T obj;
    private final PK key;
    private volatile EntryStatus status;{
        status = EntryStatus.ISNULL;
    }

    public Entry(T obj, PK key) {
        this.obj = obj;
        this.key = key;
    }

    public T getObj() {
        return obj;
    }

    public PK getKey() {
        return key;
    }

    public EntryStatus getStatus() {
        return status;
    }

    public void setStatus(EntryStatus status) {
        this.status = status;
    }
}
