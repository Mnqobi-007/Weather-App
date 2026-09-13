package com.weather.weatherapp.controller;

import com.weather.weatherapp.dto.response.WeatherRes;
import com.weather.weatherapp.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
    private WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String index() {
        return "Hello World";
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestBody String city) {
        WeatherRes res = weatherService.getWeatherByCity(city);
        return ResponseEntity.ok(res);
    }
}
