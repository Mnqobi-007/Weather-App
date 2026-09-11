package com.weather.weatherapp.dto.response;

import java.util.List;

public record GeocodeRes(
        List<Result> results
) {
    public record Result(
            String name,
            double latitude,
            double longitude,
            String country_code,
            String timezone,
            String admin1
    ) {}
}