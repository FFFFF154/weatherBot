package ru.alekseev.weatherbot.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public record CurrentWeatherDTO (@JsonProperty("weather")
                                 List<WeatherDTO.DescriptionWeather> descriptionWeathers,
                                 @JsonProperty("main")
                                 WeatherDTO.Temperatures temperatures) {
}
