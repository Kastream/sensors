package com.weather.sensors.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Sensor {
    
    private static final int MIN_TEMP = -40; 
    private static final int MAX_TEMP = +40;
    private static final int ACCURACY = 2;
    private static final int TEMP_STEP = 8;
    private static final double CHANCE_CLEAR = 0.8;
    private static final double CHANCE_RAIN_CONTINUES = 0.7;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "token")
    private String token;

    @Column(name = "server_url")
    private String serverURL;

    @Column(name = "is_registered")
    private boolean isRegistered;

    @Transient
    private WeatherData weatherData;

    @Transient
    private double sessionId;

    
    public void measure() {
        if (weatherData != null) {
            weatherData.setTemperature(generateTemperature(weatherData.getTemperature()));
            weatherData.setRaining(generateRaining(weatherData.isRaining()));
        } else {
            weatherData = new WeatherData();
            weatherData.setTemperature(generateTemperature());
            weatherData.setRaining(generateRaining());
        }
    }

    private double generateTemperature() {
        double preciseTemp = MIN_TEMP + Math.random() * (MAX_TEMP-MIN_TEMP);
        double roundedTemp = (double)(Math.ceil(preciseTemp * Math.pow(10, ACCURACY))) / Math.pow(10, ACCURACY);

        return roundedTemp;
    }
    
    private double generateTemperature(double prevTemp) {
        double preciseTemp = prevTemp + Math.random() * TEMP_STEP * 2 - TEMP_STEP;
        double roundedTemp = (double)(Math.ceil(preciseTemp * Math.pow(10, ACCURACY))) / 100;
        
        if (roundedTemp > MAX_TEMP ) return MAX_TEMP;
        if (roundedTemp < MIN_TEMP) return MIN_TEMP; 
        
        return roundedTemp;
    }

    private boolean generateRaining() {
        return (Math.random() > CHANCE_CLEAR);
    }
    
    private boolean generateRaining(boolean previous) {
        if (previous == false) return generateRaining();
        return Math.random() < CHANCE_RAIN_CONTINUES;
    }

    public boolean equals(Sensor sensor) {
        return (this.id == sensor.id && this.name.equals(sensor.name));
    }
    
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
    
    public WeatherData getWeatherData() {
        return weatherData;
    }
    
    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }
    
    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
    
    public double getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(double sessionId) {
        this.sessionId = sessionId;
    }
}
