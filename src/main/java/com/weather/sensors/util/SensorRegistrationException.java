package com.weather.sensors.util;

import org.springframework.validation.BindingResult;

public class SensorRegistrationException extends RuntimeException {
    private BindingResult bindingResult;

    public SensorRegistrationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public SensorRegistrationException(String arg0, BindingResult bindingResult) {
        super(arg0);
        this.bindingResult = bindingResult;
    }

    public SensorRegistrationException(String message) {
        super(message);
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    
}
