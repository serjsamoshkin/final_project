package persistenceSystem.util;

import persistenceSystem.PersistException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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


}
