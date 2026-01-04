package ru.alekseev.weatherbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class WeatherService {

    private final WebClient webClientCurrent;
    private final WebClient webClientDaily;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WeatherService(WebClient webClientCurrent, WebClient webClientDaily) {
        this.webClientCurrent = webClientCurrent;
        this.webClientDaily = webClientDaily;
    }

    public String getCurrentWeatherResponse() {
        try {
            String data = webClientCurrent.get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            CurrentWeatherDTO weatherDTO = objectMapper.readValue(data, CurrentWeatherDTO.class);
            StringBuilder response = new StringBuilder("Current temperature is: " + getCelsiusTemperature(weatherDTO.temperatures().temperature()) + "\n" +
                    "Feels like: " + getCelsiusTemperature(weatherDTO.temperatures().feelsLikeTemperature()) + "\n" +
                    "Pressure: " + weatherDTO.temperatures().pressure() + "\n" +
                    "Description: " + weatherDTO.descriptionWeathers().getFirst().description());
            return response.toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDailyWeatherResponse() {
        try {
            String data = webClientDaily.get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            WeatherDTO weatherDTO = objectMapper.readValue(data, WeatherDTO.class);
            StringBuilder response = new StringBuilder(weatherDTO.city().name() + "\n" +
                    "Sunrise: " + getTimeFromUnix(weatherDTO.city().sunrise()) + "\n" +
                    "Sunset: " + getTimeFromUnix(weatherDTO.city().sunset()) + "\n" + "\n");
            List<WeatherDTO.WeatherList> weatherList = weatherDTO.list().subList(0, 6);
            for (WeatherDTO.WeatherList weatherDTOList : weatherList) {
                response.append("Time: " + getTimeFromUnix(weatherDTOList.time()) + "\n" +
                        "Temperature: " + getCelsiusTemperature(weatherDTOList.temperatures().temperature()) + "\n" +
                        "Feels like temperature: " + getCelsiusTemperature(weatherDTOList.temperatures().feelsLikeTemperature()) + "\n" +
                        "Pressure: " + weatherDTOList.temperatures().pressure() + "\n" +
                        "Description: " + weatherDTOList.description().getFirst().description() + "\n" + "\n");
            }
            return response.toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTimeFromUnix(Long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        return String.format("%02d:%02d", zonedDateTime.getHour(), zonedDateTime.getMinute());
    }

    private String getCelsiusTemperature(Double temperature) {
        return String.format("%.2f", temperature - 273.15);
    }

}
