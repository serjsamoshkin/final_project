package myPersistenceSystem.sql;

import myPersistenceSystem.PersistException;
import myPersistenceSystem.annotations.Column;
import myPersistenceSystem.annotations.JoinColumn;
import myPersistenceSystem.annotations.ManyToOne;
import myPersistenceSystem.annotations.OneToOne;
import myPersistenceSystem.criteria.CriteriaBuilder;
import myPersistenceSystem.criteria.predicates.Operator;
import myPersistenceSystem.criteria.predicates.PredicateBuilder;
import myPersistenceSystem.util.Reflect;

import java.lang.reflect.Field;
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

        getRoot().setParameter(getValue());

        Operator operator = getOperator();

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getDbFieldName());
        stringBuilder.append(" ");
        stringBuilder.append(matchingMap.get(operator));

        if (operator == Operator.IN){
            stringBuilder.append(" (?) ");
        }else {
            stringBuilder.append(" ? ");
        }

        return  stringBuilder.toString();
    }


    // TODO переписать генерацию подчиненного объекта через один метод, в который передавать только тип
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
    public PredicateBuilder<T> in(String fieldName, Object value) throws PersistException {
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
