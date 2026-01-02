package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    //static: “acceso global simple”
    private static final Properties props = new Properties();

    // static: se carga una sola vez
    static {
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer config.properties", e);
        }
    }

    // private constructor: no quiero que instancien esta clase (utilitaria)
    private ConfigReader() {}

    //static: “acceso global simple”
    public static String get(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Falta la key en config.properties: " + key);
        }
        return value.trim();
    }
}

/**
 *
 */
