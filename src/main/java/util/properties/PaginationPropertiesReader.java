package util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PaginationPropertiesReader implements PropertiesReader {

    private final String path = "pagination.properties";
    private final Properties props;

    private static volatile PaginationPropertiesReader instance;

    private PaginationPropertiesReader() {

        props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream(path)) {
            props.load(resourceStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * {@inheritDoc}
     * @param key to search in the properties file
     * @return {@inheritDoc}
     */
    @Override
    public String getPropertyValue(String key) {
        return props.getProperty(key, "");
    }

    public static PaginationPropertiesReader getInstance(){

        PaginationPropertiesReader localInstance = instance;
        if (localInstance == null) {
            synchronized (PaginationPropertiesReader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new PaginationPropertiesReader();
                }
            }
        }
        return localInstance;

    }

}
