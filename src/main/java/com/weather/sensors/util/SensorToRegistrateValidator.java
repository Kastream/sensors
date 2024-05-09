package com.weather.sensors.util;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.weather.sensors.dto.SensorDTO;
import com.weather.sensors.models.Sensor;
import com.weather.sensors.repositories.SensorsRepository;

@Component
public class SensorToRegistrateValidator implements Validator {
    private final SensorsRepository sensorRepository;

    
    public SensorToRegistrateValidator(SensorsRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SensorDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SensorDTO sensorDTO = (SensorDTO) target;
        int id = sensorDTO.getId();
        String name = sensorDTO.getName();

        if (name == null && id == 0) {
            errors.rejectValue("id", null, "Please provide sensor name or id (if you provide both a sensor will be found by id only)");
            errors.rejectValue("name", null, "Please provide sensor name or id (if you provide both a sensor will be found by id only)");
            return;
        }
        
        Optional<Sensor> sensor;
        if (id != 0) {
            sensor = sensorRepository.findById(id);
            if (sensor.isEmpty()) errors.rejectValue("id", null, "Sensor was not found"); 
            return;
        } else {
            sensor = sensorRepository.findByName(name);
            if (sensor.isEmpty()) errors.rejectValue("id", null, "Sensor was not found");
            return;
        }
    }
}
