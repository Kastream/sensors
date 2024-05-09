package com.weather.sensors.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.weather.sensors.dto.SensorDTO;
import com.weather.sensors.services.SensorService;
import com.weather.sensors.util.SensorRegistrationException;
import com.weather.sensors.util.validationGroup.CreationInfo;
import com.weather.sensors.util.validationGroup.RegistrationInfo;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import com.weather.sensors.util.SensorNotValidException;

@RestController //(value = "/sensors")
@RequestMapping(value = "/sensors")
public class SensorsController {
    // private final ModelMapper modelMapper;
    // private final RestTemplate restTemplate;
    private final SensorService sensorService;

    // public SensorsController(ModelMapper modelMapper, RestTemplate restTemplate, SensorService sensorService) {
    //     this.modelMapper = modelMapper;
    //     this.restTemplate = restTemplate;
    //     this.sensorService = sensorService;
    // }
    
    public SensorsController(SensorService sensorService) {
        this.sensorService = sensorService;
    }
    
    @PostMapping(value = "/new")
    public ResponseEntity<String> addSensor(@Validated(CreationInfo.class) @RequestBody SensorDTO sensorDTO, BindingResult bindingResult) {
        sensorDTO = sensorService.create(sensorDTO, bindingResult);

        return new ResponseEntity<String>("Sensor created successfully with id: " + sensorDTO.getId() + ". Note, that the sensor was not registered at database. Please registrate", HttpStatus.OK);
    }

    @PostMapping(value = "/registrate")
    public  ResponseEntity<String> registrate(@Validated(RegistrationInfo.class) @RequestBody SensorDTO sensorDTO, BindingResult bindingResult){
        sensorService.registrate(sensorDTO, bindingResult);

        return new ResponseEntity<String>("Sensor registered successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/remove")
    public ResponseEntity<String> removeSensor(@RequestBody SensorDTO sensorDTO){
        sensorService.remove(sensorDTO);

        return new ResponseEntity<>("Sensor removed successfully", HttpStatus.OK);
    }

    @GetMapping(value = "/activate")
    public ResponseEntity<String> activate() {
        int numActivatedSensors = sensorService.activateAll();

        return new ResponseEntity<String>(numActivatedSensors + " sensors activated", HttpStatus.OK);
    }

    @GetMapping(value = "/stop")
    public ResponseEntity<String> stop(HttpServletResponse response) {
        sensorService.stopAll();
        
        response.addCookie(new Cookie("alpha", "omega"));

        return new ResponseEntity<>("All active sensors stopped", HttpStatus.OK);
    }

    @GetMapping(value = "/update-token/{id}")
    public ResponseEntity<String> updateToken(@PathVariable(value = "id") int id) {
        sensorService.updateToken(id);

        return new ResponseEntity<String>("Token updated successfully", HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = SensorNotValidException.class)
    public StringBuilder handleValidationException(SensorNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        StringBuilder errorMessage = new StringBuilder("Exceptions occuirred: ");
        for ( ObjectError error : bindingResult.getAllErrors()){
            errorMessage.append(error.getDefaultMessage()).append("; ");
        }

        return errorMessage;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = SensorRegistrationException.class)
    public Map<String, String> handleNotFoundException(SensorRegistrationException exception) {

        Map<String, String> errors = new HashMap<>();

        if (exception.getMessage() != null) errors.put("Exception", exception.getMessage());
        if (exception.getBindingResult() != null) {
            for (FieldError error : exception.getBindingResult().getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        }

        return errors; 
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public String handleRuntimeException(RuntimeException e) {
        return "an error took place: " + e.getMessage();
    }

}
