package com.example.weatherapp;

public class WeatherData {
    private final String city;
    private final double tempC;
    private final double feelsLikeC;
    private final int humidity;
    private final String description;
    private final String iconCode;

    public WeatherData(String city, double tempC, double feelsLikeC, int humidity, String description, String iconCode) {
        this.city = city;
        this.tempC = tempC;
        this.feelsLikeC = feelsLikeC;
        this.humidity = humidity;
        this.description = description;
        this.iconCode = iconCode;
    }

    public String getCity() {
        return city;
    }

    public double getTempC() {
        return tempC;
    }

    public double getFeelsLikeC() {
        return feelsLikeC;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public String getIconCode() {
        return iconCode;
    }
}