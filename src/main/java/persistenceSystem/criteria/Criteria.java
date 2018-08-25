package persistenceSystem.criteria;

import persistenceSystem.PersistException;

public interface Criteria {
    String getText() throws PersistException;
}
