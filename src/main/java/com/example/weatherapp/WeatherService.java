package com.example.weatherapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Service that fetches and parses current weather data from OpenWeatherMap.
 */
public class WeatherService {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String ICON_BASE = "https://openweathermap.org/img/wn/"; // <icon>@2x.png

    private final ApiConfig config;
    private final Gson gson = new Gson();
    private final WeatherIconCache iconCache = new WeatherIconCache();

    public WeatherService(ApiConfig config) {
        this.config = config;
    }

    /**
     * Fetch current weather for a given city name.
     *
     * @param city City name string.
     * @return WeatherData
     * @throws WeatherException on error (invalid city, network, parse, missing API key)
     */
    public WeatherData getCurrentWeather(String city) throws WeatherException {
        String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new WeatherException("API key not configured. Set OPENWEATHER_API_KEY or application.properties.");
        }

        String urlString = BASE_URL + "?q=" + encode(city) + "&appid=" + apiKey + "&units=metric";
        String json = httpGet(urlString);
        return parseWeather(json);
    }

    /**
     * Returns a JavaFX Image for the icon code, using an in-memory cache so we don't fetch multiple times.
     */
    public Image getIconImage(String iconCode) {
        if (iconCode == null || iconCode.isBlank()) return null;
        return iconCache.get(iconCode, () -> new Image(ICON_BASE + iconCode + "@2x.png", true));
    }

    private String encode(String s) {
        return s.replace(" ", "%20"); // minimal encode for spaces; for full encoding use URLEncoder if needed
    }

    private String httpGet(String urlStr) throws WeatherException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);

            int status = conn.getResponseCode();
            if (status == 404) {
                throw new WeatherException("City not found.");
            }
            if (status != 200) {
                throw new WeatherException("API error: HTTP " + status);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch (IOException e) {
            throw new WeatherException("Network error: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private WeatherData parseWeather(String json) throws WeatherException {
        try {
            JsonObject root = gson.fromJson(json, JsonObject.class);

            String city = root.get("name").getAsString();

            JsonObject main = root.getAsJsonObject("main");
            double temp = main.get("temp").getAsDouble();
            double feelsLike = main.get("feels_like").getAsDouble();
            int humidity = main.get("humidity").getAsInt();

            JsonArray weatherArr = root.getAsJsonArray("weather");
            JsonObject w = weatherArr.get(0).getAsJsonObject();
            String description = w.get("description").getAsString();
            String icon = w.get("icon").getAsString();

            return new WeatherData(city, temp, feelsLike, humidity, description, icon);
        } catch (Exception e) {
            throw new WeatherException("Failed to parse weather data.", e);
        }
    }
}