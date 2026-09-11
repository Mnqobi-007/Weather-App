package com.weather.weatherapp.service;

import com.weather.weatherapp.dto.response.GeocodeRes;
import org.springframework.web.client.RestClient;

public class GeocodeService {
    private final RestClient restClient;

    public GeocodeService(RestClient.Builder restClient) {
        this.restClient = restClient
                .baseUrl("https://geocoding-api.open-meteo.com/v1")
                .build();

    }

    public GeocodeRes search(String location){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("name", location)
                        .queryParam("count", 5)
                        .build())
                .retrieve()
                .body(GeocodeRes.class);
    }
}
