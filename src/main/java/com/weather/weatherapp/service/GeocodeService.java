package com.weather.weatherapp.service;

import com.weather.weatherapp.dto.response.GeocodeRes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GeocodeService {
    private final RestClient restClient;

    public GeocodeService(RestClient.Builder restClient) {
        this.restClient = restClient
                .baseUrl("https://geocoding-api.open-meteo.com/v1")
                .build();

    }

    public GeocodeRes.Result search(String location){
        GeocodeRes res = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("name", location)
                        .queryParam("count", 5)
                        .queryParam("language", "en")
                        .build())
                .retrieve()
                .body(GeocodeRes.class);

        if (res == null || res.results() == null || res.results().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No city found matching '" + location + "'");
        }
        return res.results().get(0);
    }
}
