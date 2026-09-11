package com.weather.weatherapp.dto.response;

import java.util.List;

public record WeatherRes(
        double latitude,
        double longitude,
        String timezone,
        String timezone_abbreviation,
        int utc_offset_seconds,
        Current current,
        Hourly hourly,
        HourlyUnits hourly_units
) {
    public record Current(
            String time,
            double temperature_2m,
            double wind_speed_10m
    ) {}

    public record Hourly(
            List<String> time,
            List<Double> temperature_2m,
            List<Double> relative_humidity_2m,
            List<Double> wind_speed_10m
    ) {}

    public record HourlyUnits(
            String temperature_2m,
            String relative_humidity_2m,
            String wind_speed_10m
    ) {}
}