package myPersistenceSystem.criteria;

import myPersistenceSystem.PersistException;
import myPersistenceSystem.criteria.predicates.PredicateBuilder;
import myPersistenceSystem.sql.MySqlJoinTable;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class CriteriaBuilder<T> implements Criteria {
    private Combinator type;
    private ArrayList<Criteria> conditions = new ArrayList<>();

    private ArrayList<JoinTable<?, ?>> joins = new ArrayList<>();

    private Class<T> entityClazz;

    private CriteriaBuilder<T> root;
    private ArrayList<Object> parameters = new ArrayList<>();

    /**
     * Root level CriteriaBuilder can only be obtained from JDBCDaoController subtypes.
     */
    public CriteriaBuilder(Class<T> entityClazz) {
        this.entityClazz = entityClazz;
    }


    protected CriteriaBuilder(Class<T> entityClazz,
                              CriteriaBuilder<T> root,
                              Combinator type,
                              Criteria... condition) {

        this(entityClazz);
        this.root = root;
        this.type = type;

        this.conditions.addAll(Arrays.asList(condition));
    }

    public abstract CriteriaBuilder<T> And(Criteria... condition);

    public abstract CriteriaBuilder<T> Or(Criteria... condition);

    public abstract<E> PredicateBuilder<E> getPredicateBuilder(Class<E> clazz) throws PersistException;

    public abstract  <L, R> CriteriaBuilder<T> addJoin(Class<L> left, String leftFieldName, Class<R> right, String rightFieldName);

    public ArrayList<JoinTable<?, ?>> getJoins() {
        return joins;
    }

    public abstract ArrayList<JoinTable<?, ?>> getTableJoins();

    protected Combinator getType() {
        return type;
    }

    protected Class<T> getEntityClazz() {
        return entityClazz;
    }

    protected ArrayList<Criteria> getConditions() {
        return conditions;
    }

    protected CriteriaBuilder<T> getRoot(){
        return root;
    }

    public final void setParameter(Object value){
        parameters.add(value);
    }

    public final ArrayList<Object> getParameters(){
        if (getRoot() == null){
            return new ArrayList<>(parameters);
        }
        return getRoot().getParameters();
    }

}
