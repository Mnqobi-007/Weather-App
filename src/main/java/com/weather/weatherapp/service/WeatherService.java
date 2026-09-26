package com.weather.weatherapp.service;

import com.weather.weatherapp.dto.response.GeocodeRes;
import com.weather.weatherapp.dto.response.WeatherRes;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
@CacheConfig(cacheNames = "currentWeather")
public class WeatherService {
    private final RestClient restClient;
    private final GeocodeService geocodeService;

    public WeatherService(RestClient.Builder restClientBuilder, GeocodeService geocodeService) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();
        this.geocodeService = geocodeService;
    }

    public WeatherRes getWeatherByLat(double lat, double lon) {
        return fetchWeather(lat, lon);
    }

    @Cacheable(key = "#city", condition = "#city != null and #city", unless = "#result == null")
    public WeatherRes getWeatherByCity(String city) {
        GeocodeRes.Result res = geocodeService.search(city);
        return fetchWeather(res.latitude(), res.longitude());
    }

    @CachePut(cacheNames = "currentWeather", key = "#result.city")
    public WeatherRes refreshWeather(String city) {
        GeocodeRes.Result res = geocodeService.search(city);
        return fetchWeather(res.latitude(), res.longitude());   // result is written to cache
    }

    private WeatherRes fetchWeather(double lat, double lon) {
        WeatherRes res = restClient.get()
                .uri(b -> b.path("/forecast")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lon)
                        .queryParam("current", "temperature_2m,wind_speed_10m")
                        .queryParam("hourly", "temperature_2m,relative_humidity_2m,wind_speed_10m")
                        .queryParam("timezone", "auto")
                        .queryParam("forecast_days", 3)
                        .build())
                .retrieve()
                .body(WeatherRes.class);

        if(res == null){
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Weather service unavailable");
        }
        return res;
    }
}