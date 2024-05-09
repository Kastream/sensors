package com.weather.sensors.util;
import org.springframework.validation.BindingResult;


public class SensorNotValidException extends RuntimeException {
    private BindingResult bindingResult;

    public SensorNotValidException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }


}
