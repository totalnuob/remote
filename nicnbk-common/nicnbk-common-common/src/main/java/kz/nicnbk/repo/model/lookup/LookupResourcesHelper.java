package kz.nicnbk.repo.model.lookup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public final class LookupResourcesHelper {

    public static final String RESOURCES_LOCATION = "/properties/lookups.properties";

    private static Properties resources = new Properties();

    static {
        try (InputStream inputStream = LookupResourcesHelper.class.getResourceAsStream(RESOURCES_LOCATION)) {
            resources.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Resource file missing", e);
        }
    }

    private LookupResourcesHelper() {
        // Prevent instantiation.
    }

    public static String getResourceValue(String key) {
        return (String) resources.get(key);
    }


}
