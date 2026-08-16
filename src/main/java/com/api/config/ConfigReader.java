package com.api.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage());
        }
    }

    public static String get(String key) {
        String v = properties.getProperty(key);
        if (v == null) throw new RuntimeException("Property '" + key + "' not found");
        return v.trim();
    }

    public static String getBaseUrl()  { return get("base.url"); }
    public static String getUsername() { return get("auth.username"); }
    public static String getPassword() { return get("auth.password"); }
    public static int    getConnectionTimeout() { return Integer.parseInt(get("connection.timeout")); }
    public static int    getSocketTimeout()     { return Integer.parseInt(get("socket.timeout")); }
}
