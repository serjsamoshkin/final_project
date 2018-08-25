package persistenceSystem.criteria;

import persistenceSystem.PersistException;
import persistenceSystem.annotations.Column;
import persistenceSystem.annotations.JoinColumn;
import persistenceSystem.criteria.predicates.PredicateBuilder;
import persistenceSystem.util.Reflect;

import java.lang.reflect.Field;
import java.util.*;

public abstract class CriteriaBuilder<T> implements Criteria {
    private Combinator type;
    private ArrayList<Criteria> conditions = new ArrayList<>();

    private ArrayList<JoinTable<?, ?>> joins = new ArrayList<>();

    private Class<T> entityClazz;

    private CriteriaBuilder<T> root;
    private ArrayList<Object> parameters = new ArrayList<>();

    private String queryText;

    private Map<String, Order> orderMap = new TreeMap<>();

    private int[] limit = new int[2];


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

    public abstract CriteriaBuilder<T> rowQuery(String query, Collection<?> params);

    public<O> CriteriaBuilder<T> orderBy(Class<O> clazz, String fieldName, Order order) {
        CriteriaBuilder<T> root = getRoot();
        if (root == null) {
            root = this;
        }
        root.orderMap.put(Reflect.getSqlFieldName(clazz, fieldName), order);

        return root;
    }

    /**
     * returns unmodifiableMap as result.
     * @return unmodifiableMap of ORDER BY commands in order of insertion
     */
    public Map<String, Order> getOrderMap(){
        return Collections.unmodifiableMap(orderMap);
    }

    public abstract String getOrderText();

    public abstract String getLimitText();

    /**
     * Sets the number of rows for output.
     * Limit [0, 20] means from 0 index to 19 index position.
     * If need limit of [50 - infinity]  - set [50, 0].
     * @param start start index. Starts from 0
     * @param end end index. Not including.
     * @return
     */
    public CriteriaBuilder<T> limit(int start, int end){
        CriteriaBuilder<T> root = getRoot();
        if (root == null) {
            root = this;
        }
        root.limit[0] = start;
        root.limit[1] = end;

        return root;
    }

    public int[] getLimit(){
       int[] limitCopy = new int[2];
        limitCopy[0] = limit[0];
        limitCopy[1] = limit[1];

       return limitCopy;
    }

    public abstract<E> PredicateBuilder<E> getPredicateBuilder(Class<E> clazz) throws PersistException;

    public abstract  <L, R> CriteriaBuilder<T> addJoin(Class<L> left, String leftFieldName, Class<R> right, String rightFieldName);

    public abstract ArrayList<JoinTable<?, ?>> getTableJoins();

    public ArrayList<JoinTable<?, ?>> getJoins() {
        return joins;
    }

    protected void setQueryText(String text){
        if (getRoot() == null){
            this.queryText = text;
        }else {
            getRoot().queryText = text;
        }
    }

    public String getQueryText() {
        if (getRoot() == null){
            return this.queryText;
        }else {
            return getRoot().queryText;
        }
    }

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
        if (getRoot() == null){
            parameters.add(value);
        }else {
            getRoot().parameters.add(value);
        }

    }

    public final ArrayList<Object> getParameters(){
        if (getRoot() == null){
            return new ArrayList<>(parameters);
        }
        return getRoot().getParameters();
    }

    public enum Order{
        ASC,
        DECS;
    }

}
