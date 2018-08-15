package myPersistenceSystem.util;

import myPersistenceSystem.PersistException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflect {

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

        Field field = null;

        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new PersistException("No field " + fieldName + " in class + " + clazz);
        }

        return field;
    }

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
