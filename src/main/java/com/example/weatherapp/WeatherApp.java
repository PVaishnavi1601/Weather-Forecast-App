package com.example.weatherapp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class WeatherApp extends Application {

    private static final String API_KEY = "56d3860b57aa7aa36ce2bc9d4c9d0b14";

    private TextField cityInput;
    private Label temperatureLabel;
    private Label weatherDescriptionLabel;
    private ImageView weatherIconView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather Forecast App");

        // Input field
        cityInput = new TextField();
        cityInput.setPromptText("Enter city name");
        cityInput.setPrefWidth(200);

        Button fetchButton = new Button("Get Weather");
        fetchButton.setOnAction(e -> fetchWeather(cityInput.getText()));

        // Toolbar buttons with icons
        Button refreshButton = createIconButton("icons/refresh.png", "Refresh", () -> {
            fetchWeather(cityInput.getText());
        });

        Button helpButton = createIconButton("icons/help.png", "Help", () -> {
            showAlert("Enter a city and click 'Get Weather' to see current conditions.");
        });

        Button locationButton = createIconButton("icons/location.png", "Use Location", () -> {
            // Placeholder — simulate location as "Chennai"
            fetchWeather("Chennai");
        });

        HBox toolbar = new HBox(10, cityInput, fetchButton, refreshButton, helpButton, locationButton);
        toolbar.setAlignment(Pos.CENTER);
        toolbar.setPadding(new Insets(15));

        // Display labels
        temperatureLabel = new Label("Temperature: ");
        weatherDescriptionLabel = new Label("Condition: ");
        weatherIconView = new ImageView();
        weatherIconView.setFitHeight(100);
        weatherIconView.setFitWidth(100);

        VBox displayBox = new VBox(10, temperatureLabel, weatherDescriptionLabel, weatherIconView);
        displayBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, toolbar, displayBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        primaryStage.setScene(new Scene(layout, 500, 400));
        primaryStage.show();
    }

    private void fetchWeather(String city) {
        if (city == null || city.isEmpty()) {
            showAlert("Please enter a city name.");
            return;
        }

        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" +
                    URLEncoder.encode(city, "UTF-8") +
                    "&appid=" + API_KEY + "&units=metric";

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();

                double temp = response.getAsJsonObject("main").get("temp").getAsDouble();
                String description = response.getAsJsonArray("weather")
                        .get(0).getAsJsonObject().get("description").getAsString();
                String iconCode = response.getAsJsonArray("weather")
                        .get(0).getAsJsonObject().get("icon").getAsString();

                temperatureLabel.setText("Temperature: " + temp + "°C");
                weatherDescriptionLabel.setText("Condition: " + description);

                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                weatherIconView.setImage(new Image(iconUrl));
            } else {
                showAlert("City not found. Please check the spelling.");
            }

        } catch (Exception e) {
            showAlert("Error fetching weather: " + e.getMessage());
        }
    }

    private Button createIconButton(String iconPath, String tooltipText, Runnable action) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/" + iconPath)));
        icon.setFitWidth(24);
        icon.setFitHeight(24);

        Button button = new Button();
        button.setGraphic(icon);
        button.setTooltip(new Tooltip(tooltipText));
        button.setOnAction(e -> action.run());
        return button;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Weather Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
