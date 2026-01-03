package utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (is == null) {
                throw new RuntimeException("config.properties not found in classpath (src/main/resources or src/test/resources)");
            }

            props.load(is);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo leer config.properties", e);
        }
    }

    public static String get(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing key in config.properties: " + key);
        }
        return value.trim();
    }
}
