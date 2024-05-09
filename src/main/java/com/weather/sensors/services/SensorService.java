package com.weather.sensors.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import com.weather.sensors.dto.SensorDTO;
import com.weather.sensors.models.Sensor;
import com.weather.sensors.repositories.SensorsRepository;
import com.weather.sensors.util.SensorDTOValidator;
import com.weather.sensors.util.SensorRegistrationException;
import com.weather.sensors.util.SensorToRegistrateValidator;
import com.weather.sensors.util.SensorNotValidException;

@Service
public class SensorService {
    private final SensorsRepository sensorsRepository;
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;
    private final SensorDTOValidator sensorDTOValidator;
    private final SensorToRegistrateValidator sensorToRegistrateValidator;
    
    private static final String SEND_MEASURE_PATH = "/measurements/add";
    private static final String REGISTRATION_PATH = "/sensors/registration";
    private static final String AUTHENTICATION_PATH = "/measurements/authenticate";
    private static final String UPDATE_TOKEN_PATH = "/update-token";
    private static final int MEASURE_FREQUENCY = 15000;
    
    private Thread measuringThread;
    
    public SensorService(SensorsRepository sensorsRepository, RestTemplate restTemplate, ModelMapper modelMapper, SensorDTOValidator sensorDTOValidator, SensorToRegistrateValidator sensorToRegistrateValidator) {
        this.sensorsRepository = sensorsRepository;
        this.restTemplate = restTemplate;
        this.modelMapper = modelMapper;
        this.sensorDTOValidator = sensorDTOValidator;
        this.sensorToRegistrateValidator = sensorToRegistrateValidator;
    }

    @Transactional
    public SensorDTO create(SensorDTO sensorDTO, BindingResult bindingResult) {
        sensorDTOValidator.validate(sensorDTO, bindingResult);
        
        if (bindingResult.hasErrors()){
            throw new SensorNotValidException(bindingResult);
        }

        Sensor sensor = modelMapper.map(sensorDTO, Sensor.class);
        sensorsRepository.save(sensor);
        
        if (sensor.getId() != 0) return modelMapper.map(sensor, SensorDTO.class);
        throw new RuntimeException("sensor id was not returned by database");
    }

    @Transactional
    public void registrate(SensorDTO sensorDTO, BindingResult bindingResult) {
        sensorToRegistrateValidator.validate(sensorDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new SensorRegistrationException(bindingResult);
        }

        String url = sensorDTO.getServerURL();
        Sensor sensor = initializeSensor(sensorDTO.getId(), sensorDTO.getName());
        sensorDTO = modelMapper.map(sensor, SensorDTO.class);
        sensorDTO.setServerURL(url);
        try {
            ResponseEntity<SensorDTO> response = restTemplate.postForEntity(sensorDTO.getServerURL() + REGISTRATION_PATH, sensorDTO, SensorDTO.class);
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new SensorRegistrationException(response.toString());
            }
            sensorDTO = response.getBody();
        }  catch (RuntimeException e ) {
            throw new SensorRegistrationException(e.getMessage());
        }

        sensor = modelMapper.map(sensorDTO, Sensor.class);
        sensor.setRegistered(true);
        
        sensorsRepository.save(sensor);
    }

    public int activateAll() {
        List<Sensor> sensors = sensorsRepository.findAll();
        checkRegistration(sensors);
        if (sensors.size() == 0) throw new NullPointerException("No registrated sensors found");
        
        authenticateSensors(sensors);
        
        MeasureRunnable measureRunnable = new MeasureRunnable(sensors);
        measuringThread = new Thread(measureRunnable);
        
        measuringThread.start();
        
        return sensors.size();
    }
    
    public void stopAll() {
        measuringThread.interrupt();
    }
    
    @Transactional
    public void updateToken(int id) {
        Sensor sensor = initializeSensor(id);
        SensorDTO sensorDTO = restTemplate.postForEntity(sensor.getServerURL() + UPDATE_TOKEN_PATH , sensor, SensorDTO.class).getBody();
        if (sensorDTO != null) {
            sensor = modelMapper.map(sensorDTO, Sensor.class);
            sensor.getToken();
            sensorsRepository.save(sensor);
        } else {
            throw new RuntimeException();
        }
    }
    
    @Transactional
    public void remove(SensorDTO sensorDTO) {
        Sensor sensor = initializeSensor(sensorDTO.getId(), sensorDTO.getName());
        sensorsRepository.deleteById(sensor.getId());
    }

    private void authenticateSensors(List<Sensor> sensors) {
        for (Sensor sensor : sensors) {
            
            String sessionCookie = restTemplate.postForEntity(sensor.getServerURL() + AUTHENTICATION_PATH, modelMapper.map(sensor, SensorDTO.class), String.class).getHeaders().getFirst(HttpHeaders.SET_COOKIE);
            if (sessionCookie == null) throw new RuntimeException("cannot authenticate sensor with id " + sensor.getId() +". Please contact admin if it keeps happening"); 
            sessionCookie = sessionCookie.replace("SESSION=", "");
            double sessionId = Double.parseDouble(sessionCookie);
            sensor.setSessionId(sessionId);
        }
    }

    private void checkRegistration(List<Sensor> sensors) {
        List<Sensor> sensorsToRemove = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if (!sensor.isRegistered()) sensorsToRemove.add(sensor);
        }
        for (Sensor sensor : sensorsToRemove) sensors.remove(sensor);
    }

    private Sensor initializeSensor(int id, String name) {
        if (id != 0) return initializeSensor(id);
        return initializeSensor(name);
    }

    private Sensor initializeSensor(int id) {
        Optional<Sensor> sensor = sensorsRepository.findById(id);
        
        return sensor.orElseThrow(() -> new SensorRegistrationException("Sensor with id: " + id + " was not found"));
    }
    
    private Sensor initializeSensor(String name) {
        Optional<Sensor> sensor = sensorsRepository.findByName(name);
        
        return sensor.orElseThrow(() -> new SensorRegistrationException("Sensor with name: " + name + " was not found"));
    }

    private class MeasureRunnable implements Runnable {
        List<Sensor> sensors;

        MeasureRunnable(List<Sensor> sensors) {
            this.sensors = sensors;
        }
        
        @Override
        public void run() {
            while( !Thread.currentThread().isInterrupted()) {
                for (Sensor sensor : sensors) {
                    sensor.measure();
                    HttpEntity<SensorDTO> httpEntity = new HttpEntity<SensorDTO>(modelMapper.map(sensor, SensorDTO.class));
                    restTemplate.postForLocation(sensor.getServerURL() + SEND_MEASURE_PATH, httpEntity);
                }
                try {
                    Thread.sleep(MEASURE_FREQUENCY);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
    
        }
    }


}


