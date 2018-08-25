package persistenceSystem.sql;

import persistenceSystem.*;
import persistenceSystem.annotations.*;
import persistenceSystem.criteria.CriteriaBuilder;
import persistenceSystem.util.Reflect;

import java.lang.Enum;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MySqlJDBCDaoController extends JDBCDaoController {


    /**
     * {@inheritDoc}
     */
    public <T, PK> T getByPK(PK key, Class<T> clazz, Connection connection) throws PersistException {
        List<T> list;

        StringBuilder sql = new StringBuilder(" SELECT ")
                .append(getSQLFieldsName(clazz))
                .append(" FROM ")
                .append(getSQLTableName(clazz))
                .append(" WHERE ")
                .append(getSQLIdName(clazz))
                .append(" = ?");

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setObject(1, key);
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs, clazz, connection);
        } catch (SQLException e) {
            throw new PersistException(e);
        }

        if (list == null || list.size() == 0) {
            return null;
        }
        if (list.size() > 1) {
            throw new PersistException("Received more than one record.");
        }
        return list.iterator().next();

    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getALL(Class<T> clazz, Connection connection) throws PersistException {
        List<T> list;

        String sql = String.format(" SELECT %s FROM %s", getSQLFieldsName(clazz), getSQLTableName(clazz));

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs, clazz, connection);
        } catch (SQLException e) {
            throw new PersistException(e);
        }

        if (list == null || list.size() == 0) {
            return null;
        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    public <T> void save(T object, Connection connection) throws PersistException, RowNotUniqueException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();

        if (getEntryByPK(clazz, getId(object)).isPresent()) {
            update(object, connection);
            return;
        }

        String sql = getInsertQuery(clazz);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            prepareStatementForInsert(statement, object);
            int count;
            try {
                count = statement.executeUpdate();
            }catch (SQLException e){
                throw new RowNotUniqueException(e, statement);
            }
            if (count != 1) {
                throw new PersistException("On insert modify more then 1 record: " + count);
            }
        } catch (SQLException e) {
            // TODO logger
            throw new PersistException(e);
        }

        String version = getVersionSQLName(clazz);
        if (!version.isEmpty()){
            version = ", " + version;
        }

        StringBuilder str = new StringBuilder(" SELECT ")
                .append(getSQLIdName(clazz))
                .append(version)
                .append(" FROM ")
                .append(getSQLTableName(clazz))
                .append(" WHERE ")
                .append(getSQLIdName(clazz))
                .append(" = last_insert_id()");

        try (PreparedStatement statement = connection.prepareStatement(str.toString())) {

            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                // TODO сделать транзакции
                //pull.rollback();
                throw new PersistException("Exception on find primary key of new persist data");
            }
            setId(object, rs.getObject(1));
            if (clazz.isAnnotationPresent(VersionControl.class)){
                setVersion(object, rs.getObject(2));
            }
        } catch (Exception e) {
            // TODO сделать транзакции
            //pull.rollback();
            throw new PersistException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public <T> void update(T object, Connection connection) throws PersistException, ConcurrentModificationException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();

        synchronized (clazz) {
            if (clazz.isAnnotationPresent(VersionControl.class)) {
                if (checkVersion(clazz, object, connection)) {
                    setVersion(object, getVersion(object) + 1);
                } else {
                    throw new ConcurrentModificationException();
                }
            }

            String sql = getUpdateQuery(clazz);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                prepareStatementForUpdate(statement, object);
                int count = statement.executeUpdate();
                if (count != 1) {
                    throw new PersistException("On update modify more then 1 record: " + count);
                }
            } catch (Exception e) {
                throw new PersistException(e);
            }
        }

        clearEntry(clazz, getId(object));
    }

    /**
     * {@inheritDoc}
     */
    public <T> void delete(T object, Connection connection) throws PersistException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();

        String sql = getDeleteQuery(clazz);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            prepareStatementForDelete(statement, object);
            int count = statement.executeUpdate();
            if (count != 1) {
                throw new PersistException("More then 1 record deleted: " + count);
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }

    }


    /**
     * {@inheritDoc}
     */
    public <T> CriteriaBuilder<T> getCriteriaBuilder(Class<T> clazz) {
        return new MySqlCriteriaBuilder<>(clazz);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getByCriteria(Class<T> clazz,
                                     CriteriaBuilder<T> criteriaBuilder,
                                     Connection connection) throws PersistException {

        List<T> list;

        StringBuilder str;
        if (criteriaBuilder.getQueryText() == null) {

            str = new StringBuilder(" SELECT ")
                    .append(getSQLFieldsName(clazz))
                    .append(" FROM ")
                    .append(getSQLTableName(clazz));

            criteriaBuilder.getTableJoins().forEach(c -> str.append(c.getText()));

            str.append(" WHERE ");
            str.append(criteriaBuilder.getText());
            str.append(criteriaBuilder.getOrderText());
            str.append(criteriaBuilder.getLimitText());

        }else {
            str = new StringBuilder(criteriaBuilder.getQueryText());
        }

        try (PreparedStatement statement = connection.prepareStatement(str.toString())) {

            List<Object> params = criteriaBuilder.getParameters();

            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof  Enum){
                    statement.setObject(i + 1, params.get(i).toString());
                }else {
                    statement.setObject(i + 1, params.get(i));
                }
            }

            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs, clazz, connection);

         } catch (SQLException e) {
            throw new PersistException(e);
        }

        return list;
    }


    private <T> boolean checkVersion(Class<T> clazz, T obj, Connection connection){
        int ver = getVersion(obj);
        StringBuilder str = new StringBuilder(" SELECT ")
                .append(getVersionSQLName(clazz))
                .append(" FROM ")
                .append(getSQLTableName(clazz))
                .append(" WHERE ")
                .append(getSQLIdName(clazz))
                .append(" = ?");

        try (PreparedStatement statement = connection.prepareStatement(str.toString())) {
            statement.setObject(1, getId(obj));
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                // TODO сделать транзакции
                //pull.rollback();
                throw new PersistException("Exception on find primary key of new persist data");
            }
            if (rs.getInt(1) != ver){
                return false;
            }

        } catch (Exception e) {
            // TODO сделать транзакции
            //pull.rollback();
            throw new PersistException(e);
        }

        return true;

    }

    /**
     * Возвращает объект из entryMap или создает новый по переданному ключу (id значению)
     * @param clazz
     * @param key
     * @param <T>
     * @param <PK>
     * @return
     * @throws PersistException
     */
    private <T, PK> Entry<T, PK> getObjectByKey(Class<T> clazz, T obj, PK key) throws PersistException {


//        Optional<Entry<T, PK>> entryOpt = getEntryByObjKey(clazz, key);
//
//        if (entryOpt.isPresent()) {
//            return entryOpt.get();
//        }else {
            try {
                return createAndGetEntry(clazz, obj, key);
            } catch (Exception e) {
                throw new PersistException(e);
            }
//        }
    }

    /**
     * Возвращает имя таблицы для SQL-запроса
     * <p/> SELECT [FieldsName] FROM [TableName]
     */
    private <T> String getSQLTableName(Class<T> clazz) throws PersistException {

        String str;

        if (clazz.isAnnotationPresent(TableName.class)) {
            str = (clazz.getAnnotation(TableName.class)).name();
        } else {
            throw new PersistException("No table name annotated in" + clazz);
        }

        return str;
    }

    /**
     * Возвращает имя поля первичного ключа для SQL-запроса
     * <p/> SELECT [FieldsName] FROM [TableName] WHERE [IdName] = ?
     */
    private <T> String getSQLIdName(Class<T> clazz) throws PersistException {
        String str = "";

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                str = field.getAnnotation(Column.class).name();
            }
        }
        if (str.isEmpty()) {
            throw new PersistException("No field is annotated as @Id.");
        }
        return str;
    }

    private <T> String getVersionSQLName(Class<T> clazz) throws PersistException {
        String str = "";

        if (clazz.isAnnotationPresent(VersionControl.class)) {
            str = Arrays.stream(clazz.getDeclaredFields()).
                    filter(f -> f.isAnnotationPresent(Version.class) && f.isAnnotationPresent(Column.class)).findFirst().
                    orElseThrow(() -> new PersistException("Incorrect annotated class with VersionControl: " + clazz))
                    .getAnnotation(Column.class).name();

//            for (Field field : clazz.getDeclaredFields()) {
//                if (field.isAnnotationPresent(Version.class)) {
//                    str = field.getAnnotation(Column.class).name();
//                }
//            }
//            if (str.isEmpty()) {
//                throw new PersistException("No field is annotated as @Version.");
//            }
        }
        return str;
    }

    private String getSqlFieldName(Field field) throws PersistException {

//        String fieldName;
//
//        if (field.isAnnotationPresent(Column.class)) {
//            fieldName = field.getAnnotation(Column.class).name();
//        } else if (field.isAnnotationPresent(JoinColumn.class)) {
//            fieldName = field.getAnnotation(JoinColumn.class).name();
//        } else {
//            throw new PersistException(
//                    String.format("Field %s doesn't annotated with Column or JoinColumn annotation in class %s.",
//                            field.getName(), field.getType()));
//        }

        return Reflect.getSqlFieldName(field);
    }

    /**
     * Возвращает строку вида "column1, column" для блока [FieldsName] SQL-запроса.
     * Так же включает id поле
     * <p/> SELECT [FieldsName] FROM [TableName] WHERE [IdName] = ?
     */
    private <T> String getSQLFieldsName(Class<T> clazz) throws PersistException {
        StringBuilder str = new StringBuilder();

        Arrays.stream(clazz.getDeclaredFields())
                .map(f -> Reflect.getSqlFieldName(f, false, ","))
                .filter(s -> !s.isEmpty()).forEach(str::append);

//        for (Field field : clazz.getDeclaredFields()) {
//            if (field.isAnnotationPresent(Column.class)) {
//                str.append(field.getAnnotation(Column.class).name());
//                str.append(",");
//            } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
//                if (!field.isAnnotationPresent(JoinColumn.class)) {
//                    throw new PersistException("There were no JoinColumn annotation in annotated with ManyToOne field");
//                }
//                str.append(field.getAnnotation(JoinColumn.class).name());
//                str.append(",");
//            }
//        }

        if (str.length() == 0) {
            throw new PersistException("No fields are annotated as Column in " + clazz.getName());
        } else {
            str.deleteCharAt(str.length() - 1);
        }

        //return "id, number, department";
        return str.toString();
    }

    /**
     * Возвращает строку вида "column1, column" для блока [FieldsName] SQL-запроса.
     * Не включает id поле
     * <p/> INSERT INTO [Table] ([FieldsName]) VALUES (?, ?, ...);
     */
    private <T> String getSQLInsertFieldsName(Class<T> clazz) throws PersistException {
        StringBuilder str = new StringBuilder();

        Arrays.stream(clazz.getDeclaredFields()).filter(f -> !f.isAnnotationPresent(Id.class))
                .map(f -> Reflect.getSqlFieldName(f, false, ","))
                .filter(s -> !s.isEmpty()).forEach(str::append);

//        for (Field field : clazz.getDeclaredFields()) {
//            if (field.isAnnotationPresent(Id.class)){
//                continue;
//            }
//            if (field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class)) {
//                str.append(field.getAnnotation(Column.class).name());
//                str.append(",");
//            } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
//                if (!field.isAnnotationPresent(JoinColumn.class)) {
//                    throw new PersistException("There were no JoinColumn annotation in annotated with ManyToOne field");
//                }
//                str.append(field.getAnnotation(JoinColumn.class).name());
//                str.append(",");
//            }
//        }

        if (str.length() == 0) {
            throw new PersistException("No fields are annotated as Column in " + clazz.getName());
        } else {
            str.deleteCharAt(str.length() - 1);
        }

        //return "number, department";
        return str.toString();
    }

    /**
     * Возвращает строку вида (?, ?, ...) для блока значений SQL-запроса.
     * по кличесву полей аннотированных Column, исключая id поле
     * <p/> INSERT INTO [Table] ([FieldsName]) VALUES (?, ?, ...);
     */
    private <T> String getSQLInsertValuesName(Class<T> clazz) throws PersistException {
        StringBuilder str = new StringBuilder();

        Arrays.stream(clazz.getDeclaredFields()).filter(f -> !f.isAnnotationPresent(Id.class))
                .map(f -> Reflect.getEmptySqlFieldName(f, false, " ?,"))
                .filter(s -> !s.isEmpty()).forEach(str::append);

//        for (Field field : clazz.getDeclaredFields()) {
//            if (field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class)) {
//                str.append("?");
//                str.append(",");
//            } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
//                if (!field.isAnnotationPresent(JoinColumn.class)) {
//                    throw new PersistException("There were no JoinColumn annotation in annotated with ManyToOne/OneToOne field");
//                }
//                str.append("?");
//                str.append(",");
//            }
//        }

        if (str.length() == 0) {
            throw new PersistException("No fields are annotated as Column in " + clazz.getName());
        } else {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }

    /**
     * возвращает часть строки SQL-запроса с конструкцией обновленяи значений
     * <p/> [column = ?, column = ?, ...]
     */
    private <T> String getSQLFieldsNameUpdate(Class<T> clazz) throws PersistException {

        StringBuilder str = new StringBuilder();

        Arrays.stream(clazz.getDeclaredFields()).filter(f -> !f.isAnnotationPresent(Id.class))
                .map(f -> Reflect.getSqlFieldName(f, false, " = ?,"))
                .filter(s -> !s.isEmpty()).forEach(str::append);

//        for (Field field : clazz.getDeclaredFields()) {
//            if (field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class)) {
//                str.append(field.getAnnotation(Column.class).name());
//                str.append(" = ?");
//                str.append(",");
//            } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
//                if (!field.isAnnotationPresent(JoinColumn.class)) {
//                    throw new PersistException("There were no JoinColumn annotation in annotated with ManyToOne fiaeld");
//                }
//                str.append(field.getAnnotation(JoinColumn.class).name());
//                str.append(" = ?");
//                str.append(",");
//            }
//        }

        if (str.length() == 0) {
            throw new PersistException("No fields are annotated as Column in " + clazz.getName());
        } else {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();

    }

    /**
     * Возвращает sql запрос для обновления записи.
     * <p/>
     * UPDATE [Table] SET [column = ?, column = ?, ...] WHERE id = ?;
     */
    private <T> String getUpdateQuery(Class<T> clazz) throws PersistException {

        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(getSQLTableName(clazz))
                .append(" SET ")
                .append(getSQLFieldsNameUpdate(clazz))
                .append(" WHERE ")
                .append(getSQLIdName(clazz))
                .append(" = ?");

        return sql.toString();
    }

    /**
     * Возвращает sql запрос для вставки новой записи.
     * <p/>
     * INSERT INTO [Table] (column, column, ...] VALUES (?, ?, ...);
     */
    private <T> String getInsertQuery(Class<T> clazz) throws PersistException {

        StringBuilder sql = new StringBuilder("INSERT INTO ")
                .append(getSQLTableName(clazz))
                .append(" ( ")
                .append(getSQLInsertFieldsName(clazz))
                .append(" ) VALUES (")
                .append(getSQLInsertValuesName(clazz))
                .append(")");

        return sql.toString();
    }

    /**
     * Возвращает sql запрос для вставки новой записи.
     * <p/>
     * INSERT INTO [Table] (column, column, ...] VALUES (?, ?, ...);
     */
    private <T> String getDeleteQuery(Class<T> clazz) throws PersistException {

        StringBuilder sql = new StringBuilder("DELETE FROM ")
                .append(getSQLTableName(clazz))
                .append(" WHERE ")
                .append(getSQLIdName(clazz))
                .append(" = ?");

        return sql.toString();
    }


    /**
     * Устанавливает аргументы update запроса в соответствии со значением полей объекта object.
     */
    private <T> void prepareStatementForUpdate(PreparedStatement statement, T object) throws PersistException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();

        AtomicInteger in = new AtomicInteger();
        Reflect.forEachSqlNamedFields(clazz, f ->
                        setValueToStatement(Reflect.getFieldValue(clazz, f, object), statement, in.incrementAndGet())
                , Id.class);

        Field fieldId =  Arrays.stream(clazz.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new PersistException(String.format("No field is annotated as Id in %s class.", clazz)));

        setValueToStatement(Reflect.getFieldValue(clazz, fieldId, object), statement, in.incrementAndGet());

//        try {
//            int i = 0;
            //Field fieldId = null;
//
//            for (Field field : clazz.getDeclaredFields()) {
//                if (field.isAnnotationPresent(Column.class)) {
//                    if (field.isAnnotationPresent(Id.class)){
//                        fieldId = field;
//                    }else if(field.isAnnotationPresent(EnumType.class)){
//                        statement.setObject(++i, Reflect.getGetterMethodByField(clazz, field).invoke(object).toString());
//                    }else {
//                        statement.setObject(++i, Reflect.getGetterMethodByField(clazz, field).invoke(object));
//                    }
//                } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
//                    if (field.isAnnotationPresent(JoinColumn.class)) {
//                        statement.setObject(++i, getId(Reflect.getGetterMethodByField(clazz, field).invoke(object)));
//                    }else {
//                        throw new PersistException("There were no JoinColumn annotation in annotated with ManyToOne field");
//                    }
//                }
//            }
//            if (fieldId != null) {
//                /* Сначала заполняются поля не идентификаторы, идентификатор получает последний номер,
//                 * так как идентификатор записи передается последним аргументом в запрос
//                 */
//                statement.setObject(in.incrementAndGet(), Reflect.getGetterMethodByField(clazz, fieldId).invoke(object));
//            } else {
//                throw new PersistException(String.format("No field is annotated as Id in %s class.", clazz));
//            }
//        } catch (Exception e) {
//            throw new PersistException(e);
//        }
    }

    /**
     * Устанавливает аргументы delete запроса в соответствии со значением id поля объекта object.
     */
    private <T> void prepareStatementForDelete(PreparedStatement statement, T object) throws PersistException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();

        Field fieldId =  Arrays.stream(clazz.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new PersistException(String.format("No field is annotated as Id in %s class.", clazz)));

        setValueToStatement(Reflect.getFieldValue(clazz, fieldId, object), statement, 1);

//        try {
//            int i = 0;
//            Field fieldId = null;
//            for (Field field : clazz.getDeclaredFields()) {
//                if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(Id.class)) {
//                    fieldId = field;
//                }
//            }
//            if (fieldId != null) {
//                /* Сначала заполняются поля не идентификаторы, идентификатор получает последний номер,
//                 * так как идентификатор записи передается последним аргументом в запрос
//                 */
//                statement.setObject(++i, Reflect.getGetterMethodByField(clazz, fieldId).invoke(object));
//            } else {
//                throw new PersistException("No field is annotated as Id in Group class.");
//            }
//        } catch (Exception e) {
//            throw new PersistException(e);
//        }


    }

    /**
     * Устанавливает аргументы update запроса в соответствии со значением полей объекта object.
     */
    private <T> void prepareStatementForInsert(PreparedStatement statement, T object) throws PersistException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();

        AtomicInteger in = new AtomicInteger();
        Reflect.forEachSqlNamedFields(clazz, f ->
                        setValueToStatement(Reflect.getFieldValue(clazz, f, object), statement, in.incrementAndGet())
                , Id.class);

//        try {
//            int i = 0;
//            for (Field field : clazz.getDeclaredFields()) {
//                if (field.isAnnotationPresent(Column.class) ) {
//                    if (field.isAnnotationPresent(EnumType.class)){
//                        statement.setObject(++i, Reflect.getGetterMethodByField(clazz, field).invoke(object).toString());
//                    }else if (!field.isAnnotationPresent(Id.class)){
//                        statement.setObject(++i, Reflect.getGetterMethodByField(clazz, field).invoke(object));
//                    }
//                } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
//                    if (field.isAnnotationPresent(JoinColumn.class)) {
//                        setPkIdToStatement(Reflect.getGetterMethodByField(clazz, field).invoke(object), statement, ++i);
//                    }else {
//                        throw new PersistException("There were no JoinColumn annotation in annotated with ManyToOne field");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new PersistException(e);
//        }
    }

    /**
     * Разбирает ResultSet и возвращает список объектов соответствующих содержимому ResultSet.
     */
    private <T> List<T> parseResultSet(ResultSet rs, Class<T> clazz, Connection connection) throws PersistException {

        List<T> list = new ArrayList<>();

        try {
            while (rs.next()) {
                Field idField = null;
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        idField = field;
                        break;
                    }
                }

                if (idField == null) {
                    throw new PersistException("No ID field in class: " + clazz);
                }

                Object key = rs.getObject(idField.getAnnotation(Column.class).name());
                T object = clazz.getConstructor().newInstance();
                Entry<T, ?> entry = getObjectByKey(clazz, object, key);
                object = entry.getObj();

                if (entry.getStatus() == EntryStatus.ISNULL) {

                    entry.setStatus(EntryStatus.CONSTRACT);

                    for (Field field : clazz.getDeclaredFields()) {
                        if (field.isAnnotationPresent(Column.class)) {
                            Method method = Reflect.getSetterMethodByField(clazz, field);
                            Object val;
                            if ((field.isAnnotationPresent(EnumType.class))){
                                val = Enum.valueOf((Class<? extends Enum>) field.getType(), rs.getObject(field.getAnnotation(Column.class).name()).toString());
                            }else {
                                val = rs.getObject(field.getAnnotation(Column.class).name());
                            }
                            method.invoke(object, val);
                        } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                            if (!field.isAnnotationPresent(JoinColumn.class)) {
                                throw new PersistException(String.format("No @JoinColumn annotation in field (with @ManyToOne/OneToOne annotation): %s ", field.getName()));
                            }
                            Method method = Reflect.getSetterMethodByField(clazz, field);

                            method.invoke(object,
                                    getByPK(rs.getObject(field.getAnnotation(JoinColumn.class).name())
                                            , field.getType(),
                                            connection));

                        } else if (field.isAnnotationPresent(OneToMany.class)) {
                            Method method = Reflect.getSetterMethodByField(clazz, field);
                            String fieldName = field.getAnnotation(OneToMany.class).mappedBy();

                            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                            Class<?> genericFieldClazz = (Class<?>) genericType.getActualTypeArguments()[0];

                            method.invoke(object, getListByFK(genericFieldClazz, object, fieldName, connection));

                        }
                    }
                    entry.setStatus(EntryStatus.NEW);
                }
                list.add(object);
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return list;

    }

    private <N, FK> List<N> getListByFK(Class<N> clazz, FK key, String fieldName, Connection connection) throws PersistException {

        List<N> list;

        StringBuilder sql = new StringBuilder(" SELECT ")
                .append(getSQLFieldsName(clazz))
                .append(" FROM ")
                .append(getSQLTableName(clazz))
                .append(" WHERE ")
                .append(getSqlFieldName(
                        Reflect.getFieldByName(
                                clazz,
                                fieldName)))
                .append(" = ?");

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            statement.setObject(1, getId(key));
            ResultSet rs = statement.executeQuery();
            list = parseResultSet(rs, clazz, connection);
        } catch (SQLException e) {
            throw new PersistException(e);
        }

        if (list == null || list.size() == 0) {
            return null;
        }

        return list;

    }

    private <T, PK> void setId(T object, PK key) throws PersistException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();

        Field field = Arrays.stream(clazz.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new PersistException("No @Id in class: " + clazz.getName()));

        try {
            Method method = Reflect.getSetterMethodByField(clazz, field);
            method.invoke(object, key);
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    private <T> void setVersion(T object, Object ver) throws PersistException {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) object.getClass();

        Field field = Arrays.stream(clazz.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Version.class))
                .findFirst().orElseThrow(() -> new PersistException("No @Version in class: " + clazz.getName()));

        try {
            Method method = Reflect.getSetterMethodByField(clazz, field);
            method.invoke(object, ver);

        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T, PK> PK getId(T object) throws PersistException {
        return Reflect.getId(object);
    }

    @SuppressWarnings("unchecked")
    private <T> int getVersion(T object) throws PersistException {

        int value = 0;

        Class<T> clazz = (Class<T>)object.getClass();

        Field field = Arrays.stream(clazz.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Version.class))
                .findFirst().orElseThrow(() -> new PersistException("No @Version in class: " + clazz.getName()));
        value = (int) Reflect.getFieldValue(clazz, field, object);

        return value;
    }

    private void setValueToStatement(Object value, PreparedStatement statement, int i) throws PersistException {
        try {
            statement.setObject(i, value);
        } catch (SQLException e) {
            throw new PersistException(e);
        }

    }


}
