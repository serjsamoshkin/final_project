package persistenceSystem.criteria.predicates;

import persistenceSystem.PersistException;
import persistenceSystem.criteria.Criteria;
import persistenceSystem.criteria.CriteriaBuilder;

import java.util.Collection;

/**
 * Base class for all Predicates in Criteria object
 * @param <T> entry type
 */
public abstract class PredicateBuilder<T> implements Criteria {

    private Class<T> entityClazz;
    private Operator operator;

    private String dbFieldName;

    private CriteriaBuilder<T> root;

    private Object value;

    /**
     * Root level PredicateBuilder can only be obtained from JDBCDaoController subtypes.
     */
    protected PredicateBuilder(Class<T> entityClazz, CriteriaBuilder root) {

        this.entityClazz = entityClazz;
        this.root = root;
    }

    protected PredicateBuilder(Class<T> entityClazz,
                               CriteriaBuilder root,
                               Operator operator,
                               String dbFieldName,
                               Object value) {

        this(entityClazz, root);

        this.operator = operator;
        this.dbFieldName = dbFieldName;
        this.value = value;
    }

    protected Class<T> getEntityClazz() {
        return entityClazz;
    }

    protected Object getValue() {
        return value;
    }

    protected void setValue(Object value) {
        this.value = value;
    }

    protected String getDbFieldName() {
        return dbFieldName;
    }

    protected Operator getOperator() {
        return operator;
    }

    public abstract PredicateBuilder<T> equal(String fieldName, Object value) throws PersistException;

    public abstract PredicateBuilder<T> notEqual(String fieldName, Object value) throws PersistException;

    public abstract PredicateBuilder<T> greater(String fieldName, Object value) throws PersistException;

    public abstract PredicateBuilder<T> less(String fieldName, Object value) throws PersistException;

    public abstract PredicateBuilder<T> greaterEqual(String fieldName, Object value) throws PersistException;

    public abstract PredicateBuilder<T> lessEqual(String fieldName, Object value) throws PersistException;

    public abstract PredicateBuilder<T> in(String fieldName, Collection<?> value) throws PersistException;

    protected CriteriaBuilder<T> getRoot(){
        return root;
    }

}
