package com.example.weatherapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherApp extends Application {

    private String apiKey;
    private String units;

    @Override
    public void start(Stage primaryStage) {
        loadConfig();  // Load API key and units

        Label cityLabel = new Label("Enter City:");
        TextField cityInput = new TextField();
        Button getWeatherButton = new Button("Get Weather");
        Label resultLabel = new Label();

        getWeatherButton.setOnAction(event -> {
            String city = cityInput.getText().trim();
            if (city.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Error", "City name cannot be empty.");
                return;
            }

            try {
                String weather = fetchWeather(city);
                resultLabel.setText(weather);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch weather data.\n" + e.getMessage());
            }
        });

        HBox inputBox = new HBox(10, cityLabel, cityInput, getWeatherButton);
        VBox root = new VBox(15, inputBox, resultLabel);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 400, 150);
        primaryStage.setTitle("Weather Forecast App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadConfig() {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            apiKey = props.getProperty("api.key");
            units = props.getProperty("units", "metric");
        } catch (IOException | NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Config Error", "Could not load application.properties: " + e.getMessage());
        }
    }

    private String fetchWeather(String city) throws IOException {
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=" + units;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("API request failed with response code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
        String weatherDescription = jsonObject.getAsJsonArray("weather")
                                              .get(0).getAsJsonObject()
                                              .get("description").getAsString();
        double temp = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();

        return "Weather in " + city + ": " + weatherDescription + ", " + temp + "°C";
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
