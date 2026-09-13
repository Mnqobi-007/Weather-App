package com.weather.weatherapp.controller;

import com.weather.weatherapp.dto.request.GeocodeReq;
import com.weather.weatherapp.dto.response.WeatherRes;
import com.weather.weatherapp.service.WeatherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

//    @GetMapping("/")
//    public String index() {
//        return "Hello World";
//    }

    @PostMapping("/search")
    public ResponseEntity<WeatherRes> search(@Valid @RequestBody GeocodeReq req) {
        WeatherRes res = weatherService.getWeatherByCity(req.city());
        return ResponseEntity.ok(res);
    }
}
