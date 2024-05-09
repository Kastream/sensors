package com.weather.sensors.dto;

import com.weather.sensors.models.WeatherData;
import com.weather.sensors.util.validationGroup.CreationInfo;
import com.weather.sensors.util.validationGroup.RegistrationInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Valid
public class SensorDTO {

    private int id;

    @Size(min = 3, max = 30, groups = CreationInfo.class, message = "name has to be between 3 and 30 characters")
    private String name;

    private String description;

    private String token;

    @NotBlank(groups = RegistrationInfo.class, message = "serverURL must be provided for registration" )
    private String serverURL;

    private boolean isRegistered;

    private WeatherData weatherData;

    private double sessionId;

    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getServerURL() {
        return serverURL;
    }
    
    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public boolean isRegistered() {
        return isRegistered;
    }
    
    public void setRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
    
    public WeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }
    
    public double getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(double sessionId) {
        this.sessionId = sessionId;
    }
}
