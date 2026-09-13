package com.weather.weatherapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GeocodeReq (
    @NotBlank(message = "Search cannot be null")
    @Size(max = 100)
    String city
){}
