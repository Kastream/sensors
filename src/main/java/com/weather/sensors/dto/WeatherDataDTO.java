package com.weather.sensors.dto;

public class WeatherDataDTO {
    double temperature;
    boolean isRaining;
    
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
