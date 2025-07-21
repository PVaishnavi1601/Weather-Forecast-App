package com.example.weatherapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {

    private static final String ENV_NAME = "OPENWEATHER_API_KEY";
    private static final String PROP_FILE = "/application.properties"; // classpath
    private static final String PROP_KEY = "api.key";

    private String apiKey;

    public ApiConfig() {
        load();
    }

    private void load() {
        // 1. Environment variable wins
        String env = System.getenv(ENV_NAME);
        if (env != null && !env.isBlank()) {
            apiKey = env.trim();
            return;
        }

        // 2. application.properties fallback
        try (InputStream in = getClass().getResourceAsStream(PROP_FILE)) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                String prop = props.getProperty(PROP_KEY);
                if (prop != null && !prop.isBlank()) {
                    apiKey = prop.trim();
                }
            }
        } catch (IOException ignored) {
        }
    }

    public String getApiKey() {
        return apiKey;
    }
}