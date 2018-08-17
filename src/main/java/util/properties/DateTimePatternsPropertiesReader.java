package util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DateTimePatternsPropertiesReader implements PropertiesReader {

    private final String path = "dateTimePatterns.properties";
    private final Properties props;

    private static volatile DateTimePatternsPropertiesReader instance;

    private DateTimePatternsPropertiesReader() {

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

    public static DateTimePatternsPropertiesReader getInstance(){

        DateTimePatternsPropertiesReader localInstance = instance;
        if (localInstance == null) {
            synchronized (DateTimePatternsPropertiesReader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DateTimePatternsPropertiesReader();
                }
            }
        }
        return localInstance;

    }

}
