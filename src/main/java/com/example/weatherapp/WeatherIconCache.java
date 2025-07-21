package com.example.weatherapp;

import javafx.scene.image.Image;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class WeatherIconCache {

    private final Map<String, Image> cache = new ConcurrentHashMap<>();

    /**
     * Return cached image by key, or load using the supplied loader and cache it.
     */
    public Image get(String key, Supplier<Image> loader) {
        return cache.computeIfAbsent(key, k -> loader.get());
    }
}