package ru.alekseev.weatherbot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherDTO(@JsonProperty("city")
                         City city,
                         @JsonProperty("list")
                         List<WeatherList> list) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record City(@JsonProperty("name") String name,
                        @JsonProperty("sunrise") Long sunrise,
                        @JsonProperty("sunset") Long sunset) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WeatherList(@JsonProperty("dt") Long time,
                               @JsonProperty("main") Temperatures temperatures,
                               @JsonProperty("weather") List<DescriptionWeather> description){

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Temperatures(@JsonProperty("temp") Double temperature,
                               @JsonProperty("feels_like") Double feelsLikeTemperature,
                               @JsonProperty("pressure") Double pressure) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DescriptionWeather(@JsonProperty("main") String description) {

    }

//    @JsonIgnoreProperties(ignoreUnknown = true)
//    private record Description(@JsonProperty("main") String description) {
//
//    }
}
