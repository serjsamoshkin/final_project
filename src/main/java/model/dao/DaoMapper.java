package model.dao;

import persistenceSystem.JDBCDaoController;

import java.util.concurrent.ConcurrentHashMap;

public class DaoMapper {

    private static DaoMapper mapper;

    private final JDBCDaoController jdbcDaoController;

    private ConcurrentHashMap<Class<? extends GenericDAO>, GenericDAO> daoMap;{
        daoMap = new ConcurrentHashMap<>();
    }

    private DaoMapper(JDBCDaoController jdbcDaoController) {
        this.jdbcDaoController = jdbcDaoController;
    }

    /**
     * Singleton. Method builds DaoMapper with JDBCDaoController parameters.
     * Use the getMapper() method to obtain an instance of DaoMapper
     *
     * @param jdbcDaoController stored in class variable
     */
    public static void buildMapper(JDBCDaoController jdbcDaoController){
        DaoMapper localInstance = mapper;
        if (localInstance == null) {
            synchronized (DaoMapper.class) {
                localInstance = mapper;
                if (localInstance == null) {
                    mapper = new DaoMapper(jdbcDaoController);
                }
            }
        }
    }

    /**
     * Returns an instance of DaoMapper created by buildMapper(...) static method
     *
     * @return {@code DaoMapper} that was built using buildMapper method
     * @throws IllegalArgumentException if mapper wasn't built using buildMapper method
     */
    public static DaoMapper getMapper(){
        if (mapper == null){
            throw new IllegalArgumentException("Mapper not yet built!");
        }
        return mapper;
    }

    public<T extends GenericDAO> T getDao(final Class<T> clazz){

        GenericDAO dao = daoMap.get(clazz);
        if (dao == null){
            synchronized (clazz){
                dao = daoMap.get(clazz);
                if (dao == null){
                    try {
                        dao = clazz.getConstructor(JDBCDaoController.class).newInstance(jdbcDaoController);
                        daoMap.put(clazz, dao);
                    }catch (ReflectiveOperationException e){
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return (T)dao;
    }

}
