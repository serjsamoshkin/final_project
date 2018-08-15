package myPersistenceSystem.sql;

import myPersistenceSystem.PersistException;
import myPersistenceSystem.criteria.Combinator;
import myPersistenceSystem.criteria.Criteria;
import myPersistenceSystem.criteria.CriteriaBuilder;
import myPersistenceSystem.criteria.JoinTable;
import myPersistenceSystem.criteria.predicates.PredicateBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MySqlCriteriaBuilder<T> extends CriteriaBuilder<T> {

    static Map<Combinator, String> matchingMap;
    static {
        matchingMap = new HashMap<>();
        matchingMap.put(Combinator.AND, "AND");
        matchingMap.put(Combinator.OR, "OR");
    }

    /**
     * Root level CriteriaBuilder can get only from JDBCDaoController subtypes.
     */
    protected MySqlCriteriaBuilder(Class<T> clazz) {
        super(clazz);
    }


    private MySqlCriteriaBuilder(Class<T> clazz,
                                 CriteriaBuilder<T> root,
                                 Combinator type,
                                 Criteria... condition) {
        super(clazz, root, type, condition);
    }


    public MySqlCriteriaBuilder<T> And(Criteria... condition) {
        return new MySqlCriteriaBuilder<>(getEntityClazz(),
                this,
                Combinator.AND,
                condition);
    }

    public MySqlCriteriaBuilder<T> Or(Criteria... condition) {
        return new MySqlCriteriaBuilder<>(getEntityClazz(),
                this,
                Combinator.OR,
                condition);
    }

    @Override
    public <E> PredicateBuilder<E> getPredicateBuilder(Class<E> clazz) throws PersistException {
        return new MySqlPredicateBuilder<E>(clazz, this);
    }

    @Override
    public <L, R> CriteriaBuilder<T> addJoin(Class<L> left, String leftFieldName, Class<R> right, String rightFieldName){
        if (getRoot() == null) {
            this.getJoins().add(new MySqlJoinTable<>(left, leftFieldName, right, rightFieldName));
            return this;
        }else {
            getRoot().getJoins().add(new MySqlJoinTable<>(left, leftFieldName, right, rightFieldName));
            return getRoot();
        }
    }


    public ArrayList<JoinTable<?, ?>> getTableJoins() {
        if (getRoot() == null) {
            return this.getJoins();
        }else {
            return getRoot().getJoins();
        }
    }

    @Override
    public String getText() throws PersistException {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("(");

        ArrayList<Criteria> conditions = getConditions();

        Iterator<Criteria> iterator = conditions.iterator();

        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().getText());
            if (iterator.hasNext()) {
                stringBuilder.append(" ");
                stringBuilder.append(matchingMap.get(getType()));
                stringBuilder.append(" ");
            }
        }

        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
