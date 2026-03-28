package seedu.address.routing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads OpenRouteService configuration from config.properties on the classpath.
 */
public class AppConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = AppConfig.class.getResourceAsStream("/config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            System.err.println("Could not load config.properties: " + e.getMessage());
        }
    }

    public static String orsBaseUrl() {
        return props.getProperty("ORS_BASE_URL", "https://api.openrouteservice.org");
    }

    public static String orsApiKey() {
        return props.getProperty("ORS_API_KEY", "");
    }
}
