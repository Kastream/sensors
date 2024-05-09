package com.weather.sensors.models;

public class WeatherData {
    private double temperature;
    private boolean isRaining;
    
    public double getTemperature() {
        return temperature;
    }
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    public boolean isRaining() {
        return isRaining;
    }
    public void setRaining(boolean isRaining) {
        this.isRaining = isRaining;
    }
}
