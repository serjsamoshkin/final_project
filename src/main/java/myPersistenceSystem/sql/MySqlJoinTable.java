package myPersistenceSystem.sql;

import myPersistenceSystem.PersistException;
import myPersistenceSystem.annotations.Column;
import myPersistenceSystem.annotations.JoinColumn;
import myPersistenceSystem.annotations.TableName;
import myPersistenceSystem.criteria.JoinTable;

import java.lang.reflect.Field;

public class MySqlJoinTable<L, R> extends JoinTable<L, R> {

    public MySqlJoinTable(Class<L> leftClazz, String leftFieldName, Class<R> rightClazz, String rightFieldName) {
        super(leftClazz, leftFieldName, rightClazz, rightFieldName);
    }

    @Override
    public String getText() throws PersistException {
        StringBuilder str = new StringBuilder(" JOIN ");
        str.append(getSqlTableName(getRightClazz()));
        str.append(" ON ");
        str.append(getSqlFieldName(getLeftClazz(), getLeftFieldName()));
        str.append(" = ");
        str.append(getSqlFieldName(getRightClazz(), getRightFieldName()));

        return str.toString();
    }

    private<T> String getSqlFieldName(Class<T> clazz, String fieldName){
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field.isAnnotationPresent(Column.class)){
                return field.getAnnotation(Column.class).name();
            }else if (field.isAnnotationPresent(JoinColumn.class)) {
                return  field.getAnnotation(JoinColumn.class).name();
            }else {
                throw new PersistException("Field %s does not annotated with @Column or @JoinColumn");
            }
        }catch (NoSuchFieldException e){
            throw new PersistException(String.format("No field %s in class %s", getLeftFieldName(), getLeftClazz().getName()));
        }
    }

    private<T> String getSqlTableName(Class<T> clazz){
        if (clazz.isAnnotationPresent(TableName.class)){
            return clazz.getAnnotation(TableName.class).name();
        }else {
            throw new PersistException("No @TableName annotation on class " + clazz.getName());
        }
    }
}
