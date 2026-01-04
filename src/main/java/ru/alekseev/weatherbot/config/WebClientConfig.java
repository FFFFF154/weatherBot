package ru.alekseev.weatherbot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.alekseev.weatherbot.WeatherProperties;

@Configuration
public class WebClientConfig {

    private final WeatherProperties weatherProperties;

    @Autowired
    public WebClientConfig(WeatherProperties weatherProperties) {
        this.weatherProperties = weatherProperties;
    }

    @Bean
    public WebClient webClientCurrent() {
        return WebClient.builder()
                .baseUrl(weatherProperties.getCurrentWeatherUrl())
                .build();
    }

    @Bean
    public WebClient webClientDaily(){
        return WebClient.builder()
                .baseUrl(weatherProperties.getDailyWeatherUrl())
                .build();
    }
}
