package com.weather.weatherapp.service;

import com.weather.weatherapp.dto.response.WeatherRes;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {
    private final RestClient restClient;

    public WeatherService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();
    }

    public WeatherRes getWeather(double lat, double lon) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lon)
                        .queryParam("current", "temperature_2m,wind_speed_10m")
                        .queryParam("hourly", "temperature_2m,relative_humidity_2m,wind_speed_10m")
                        .queryParam("timezone", "auto")
                        .queryParam("forecast_days", 3)
                        .build())
                .retrieve()
                .body(WeatherRes.class);
    }
}