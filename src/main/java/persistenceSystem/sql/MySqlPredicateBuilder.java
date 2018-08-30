package persistenceSystem.sql;

import persistenceSystem.PersistException;
import persistenceSystem.annotations.Column;
import persistenceSystem.annotations.JoinColumn;
import persistenceSystem.annotations.ManyToOne;
import persistenceSystem.annotations.OneToOne;
import persistenceSystem.criteria.CriteriaBuilder;
import persistenceSystem.criteria.predicates.Operator;
import persistenceSystem.criteria.predicates.PredicateBuilder;
import persistenceSystem.util.Reflect;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MySqlPredicateBuilder<T> extends PredicateBuilder<T> {

    static Map<Operator, String> matchingMap;

    static {
        matchingMap = new HashMap<>();
        matchingMap.put(Operator.EQUAL, "=");
        matchingMap.put(Operator.NOT_EQUAL, "<>");
        matchingMap.put(Operator.LESS, "<");
        matchingMap.put(Operator.LESS_EQUAL, "<=");
        matchingMap.put(Operator.GREATER, ">");
        matchingMap.put(Operator.GREATER_EQUAL, ">=");
        matchingMap.put(Operator.IN, "IN");



    }

    /**
     * Root level PredicateBuilder can get only from JDBCDaoController subtypes.
     */
    protected MySqlPredicateBuilder(Class<T> clazz, CriteriaBuilder root){
        super(clazz, root);
    }

    private MySqlPredicateBuilder(Class<T> clazz,
                                  CriteriaBuilder root,
                                  Operator operator,
                                  String dbFieldName,
                                  Object value) {



        super(clazz, root, operator, dbFieldName, value);
    }


    /**
     * Generates the query text in WHERE clause and puts the parameter value in ArrayList
     * in the order of inserting the generated parameter "?".
     * @return
     * @throws PersistException
     */
    @Override
    public String getText() throws PersistException {


        Operator operator = getOperator();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getDbFieldName());
        stringBuilder.append(" ");
        stringBuilder.append(matchingMap.get(operator));

        if (operator == Operator.IN){
            stringBuilder.append(" (");
            Object local = getValue();
            if (local instanceof Collection){
                if (((Collection) local).isEmpty()){
                    throw new PersistException("Collection passed to 'In' predicate is empty!");
                }
                for (Object el:
                        (Collection)local) {
                    stringBuilder.append("?,");
                    getRoot().setParameter(el);
                }
                stringBuilder.deleteCharAt(stringBuilder.length()-1);
                stringBuilder.append(") ");
            }else {
                throw new PersistException("value passed to 'In' predicate is not collection!" );
            }

        }else {
            stringBuilder.append(" ? ");
            getRoot().setParameter(getValue());
        }

        return  stringBuilder.toString();
    }


    @Override
    public PredicateBuilder equal(String fieldName, Object value) throws PersistException {
        return new MySqlPredicateBuilder<>(
                getEntityClazz(),
                getRoot(),
                Operator.EQUAL,
                getMySqlFieldName(fieldName),
                value
        );
    }

    @Override
    public PredicateBuilder<T> notEqual(String fieldName, Object value) throws PersistException {
        return new MySqlPredicateBuilder<>(
                getEntityClazz(),
                getRoot(),
                Operator.NOT_EQUAL,
                getMySqlFieldName(fieldName),
                value
        );
    }

    @Override
    public PredicateBuilder<T> greater(String fieldName, Object value) throws PersistException {
        return new MySqlPredicateBuilder<>(
                getEntityClazz(),
                getRoot(),
                Operator.GREATER,
                getMySqlFieldName(fieldName),
                value
        );
    }

    @Override
    public PredicateBuilder<T> less(String fieldName, Object value) throws PersistException {
        return new MySqlPredicateBuilder<>(
                getEntityClazz(),
                getRoot(),
                Operator.LESS,
                getMySqlFieldName(fieldName),
                value
        );
    }

    @Override
    public PredicateBuilder<T> greaterEqual(String fieldName, Object value) throws PersistException {
        return new MySqlPredicateBuilder<>(
                getEntityClazz(),
                getRoot(),
                Operator.GREATER_EQUAL,
                getMySqlFieldName(fieldName),
                value
        );
    }

    @Override
    public PredicateBuilder<T> lessEqual(String fieldName, Object value) throws PersistException {
        return new MySqlPredicateBuilder<>(
                getEntityClazz(),
                getRoot(),
                Operator.LESS_EQUAL,
                getMySqlFieldName(fieldName),
                value
        );
    }

    @Override
    public PredicateBuilder<T> in(String fieldName, Collection<?> value) throws PersistException {
        return new MySqlPredicateBuilder<>(
                getEntityClazz(),
                getRoot(),
                Operator.IN,
                getMySqlFieldName(fieldName),
                value
        );
    }


    private String getMySqlFieldName(String fieldName) throws PersistException {

        String dbField;
        Field field = Reflect.getFieldByName(getEntityClazz(), fieldName);
        if (field.isAnnotationPresent(Column.class)){
            dbField = field.getAnnotation(Column.class).name();
        }else if ((field.isAnnotationPresent(ManyToOne.class) ||  field.isAnnotationPresent(OneToOne.class))
                && field.isAnnotationPresent(JoinColumn.class)){
            dbField = field.getAnnotation(JoinColumn.class).name();
        }
        else {
            throw new PersistException(
                    String.format("Field %s doesn't annotated with Column or (ManyToOne + JoinColumn) annotation", fieldName)
            );
        }


        return dbField;
    }

}
