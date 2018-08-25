package persistenceSystem.util;

import persistenceSystem.PersistException;
import persistenceSystem.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;

public class Reflect {

    /**
     * Gets {@code Method} using reflection. The method name is created with "get" plus name of the input
     * parameter {@code field}
     * @param clazz Class instance that contains input {@code field}
     * @param field if the field is from another class method will throw an PersistException
     * @return {@code Method}
     * @throws PersistException
     */
    public static Method getGetterMethodByField(Class<?> clazz, Field field) throws PersistException {

        StringBuilder str = new StringBuilder(field.getName());
        str.replace(0, 1, Character.toString(str.charAt(0)).toUpperCase());
        str.insert(0, "get");

        try {
            return clazz.getMethod(str.toString());
        } catch (NoSuchMethodException e) {
            throw new PersistException("No getter fo field: " + field);
        }

    }

    public static Field getFieldByName(Class<?> clazz, String fieldName) throws PersistException {

        Field field;

        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new PersistException("No field " + fieldName + " in class + " + clazz);
        }

        return field;
    }

    /**
     * Gets {@code Method} using reflection. The method name is created with "set" plus name of the input
     * parameter {@code field}
     *
     * @param clazz Class instance that contains input {@code field}
     * @param field if the field is from another class method will throw an PersistException
     * @return {@code Method}
     * @throws PersistException
     */
    public static Method getSetterMethodByField(Class<?> clazz, Field field) throws PersistException {

        StringBuilder str = new StringBuilder(field.getName());
        str.replace(0, 1, Character.toString(str.charAt(0)).toUpperCase());
        str.insert(0, "set");

        try {
            return clazz.getMethod(str.toString(), field.getType());
        } catch (NoSuchMethodException e) {
            throw new PersistException("No setter fo field: " + field);
        }
    }

    public static String getSqlFieldName(Field field, boolean chk, String delimiter) throws PersistException {

        if (field.isAnnotationPresent(Column.class)) {
            return field.getAnnotation(Column.class).name() + delimiter;
        } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
            if (field.isAnnotationPresent(JoinColumn.class)) {
                return field.getAnnotation(JoinColumn.class).name() + delimiter;
            }else {
                throw new PersistException(String.format("No @JoinColumn annotation in annotated with @ManyToOne/@OneToOne field: %s in class %s", field.getName(), field.getType()));
            }
        } else {
            if (chk){
                throw new PersistException(String.format("No annotation  @Column or @JoinColumn present on field %s in class %s", field.getName(), field.getType()));
            }
            return  "";
        }
    }

    public static String getEmptySqlFieldName(Field field, boolean chk, String delimiter) throws PersistException {
        if (field.isAnnotationPresent(Column.class)) {
            return  delimiter;
        } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
            if (field.isAnnotationPresent(JoinColumn.class)) {
                return delimiter;
            }else {
                throw new PersistException(String.format("No @JoinColumn annotation in annotated with @ManyToOne/@OneToOne field: %s in class %s", field.getName(), field.getType()));
            }
        } else {
            if (chk){
                throw new PersistException(String.format("No annotation  @Column or @JoinColumn present on field %s in class %s", field.getName(), field.getType()));
            }
            return  "";
        }
    }

    public static <T> void forEachSqlNamedFields(Class<T> clazz, Consumer<Field> action, Class<? extends Annotation> excluding ){


        Arrays.stream(clazz.getDeclaredFields()).filter(f -> !f.isAnnotationPresent(excluding))
                .filter(f -> getEmptySqlFieldName(f, false, ".").equals("."))
                .forEach(action);


    }

    public static <T> void forEachSqlNamedFields(Class<T> clazz, Consumer<Field> action ){

        Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> getEmptySqlFieldName(f, false, ".").equals("."))
                .forEach(action);


    }

    public static Object getFieldValue(Class<?> clazz, Field field, Object object) throws PersistException{

        try {
            if (field.isAnnotationPresent(Column.class)) {
                if(field.isAnnotationPresent(EnumType.class)){
                     return Reflect.getGetterMethodByField(clazz, field).invoke(object).toString();
                }else {
                     return Reflect.getGetterMethodByField(clazz, field).invoke(object);
                }
            } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    return getId(Reflect.getGetterMethodByField(clazz, field).invoke(object));
                }else {
                    throw new PersistException("There were no JoinColumn annotation in annotated with ManyToOne field");
                }
            }else {
                throw new PersistException(String.format("Field doesn't annotated SQL field named annotations. Field: %s, class %s", field.getName(), field.getType()));
            }
        }catch (ReflectiveOperationException e){
            throw new PersistException(e);
        }

    }

    @SuppressWarnings("unchecked")
    public static  <T, PK> PK getId(T object) throws PersistException {

        PK value = null;

        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                try {
                    value = (PK) Reflect.getGetterMethodByField(object.getClass(), field).invoke(object);
                    break;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new PersistException(e);
                }
            }
        }
        return value;
    }


    public static String getSqlFieldName(Field field) throws PersistException {
        return getSqlFieldName(field, true, "");
    }

    public static String getSqlFieldName(Class<?> clazz, String field) throws PersistException{
        return getSqlFieldName(getFieldByName(clazz, field));
    }



}
