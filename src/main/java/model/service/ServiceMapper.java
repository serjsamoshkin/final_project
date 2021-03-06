package model.service;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceMapper {

    private static ServiceMapper mapper;

    private final DataSource dataSource;

    private ConcurrentHashMap<Class<? extends AbstractService>, AbstractService> services;{
        services = new ConcurrentHashMap<>();
    }

    private ServiceMapper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Run after DaoMapper.buildMapper().
     *
     * Singleton. Method builds ServiceMapper with ServletContext and DataSource parameters.
     * Use the getMapper() method to obtain an instance of ServiceMapper
     *
     * @param dataSource stored in class variable
     */
    public static void buildMapper(DataSource dataSource){
        ServiceMapper localInstance = mapper;
        if (localInstance == null) {
            synchronized (ServiceMapper.class) {
                localInstance = mapper;
                if (localInstance == null) {
                    mapper = new ServiceMapper( dataSource);
                }
            }
        }
    }

    /**
     * Returns an instance of ServiceMapper created by buildMapper(...) static method
     *
     * @return {@code ServiceMapper} that was built using buildMapper method
     * @throws IllegalArgumentException if mapper wasn't built using buildMapper method
     */
    public static ServiceMapper getMapper(){
        if (mapper == null){
            throw new IllegalArgumentException("Mapper not yet built!");
        }
        return mapper;
    }

    public<T extends AbstractService> T getService(final Class<T> clazz){

        AbstractService service = services.get(clazz);
        if (service == null){
            synchronized (clazz){
                service = services.get(clazz);
                if (service == null){
                    try {
                        service = clazz.getConstructor(DataSource.class).newInstance(dataSource);
                        services.put(clazz, service);
                    }catch (ReflectiveOperationException e){
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return (T)service;
    }

}
