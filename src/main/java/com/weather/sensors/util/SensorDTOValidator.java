package com.weather.sensors.util;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.weather.sensors.dto.SensorDTO;
import com.weather.sensors.models.Sensor;
import com.weather.sensors.repositories.SensorsRepository;

@Component
public class SensorDTOValidator implements Validator {
    private final SensorsRepository sensorRepository;

    public SensorDTOValidator(SensorsRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SensorDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SensorDTO sensorDTO = (SensorDTO) target;

        Optional<Sensor> sensorOptional = sensorRepository.findByName(sensorDTO.getName());
        if (sensorOptional.isPresent()) errors.rejectValue("name", null, "Sensor with this name already exists: " + sensorDTO.getName());

        
    }
    
}
