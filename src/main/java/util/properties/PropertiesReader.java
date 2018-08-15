package util.properties;

public interface PropertiesReader {

    /**
     * Returns the value of the property key or an empty String ("") if there is no desired key in the property file.
     * @param key to search in the properties file
     * @return {@code String} value of the key in the properties file
     */
    String getPropertyValue(String key);

}
