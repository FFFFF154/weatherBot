package ru.alekseev.weatherbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "weather") // тот самый префикс
@Data // lombok
@PropertySource("classpath:application.yml")
public class WeatherProperties {

    private String key;
    private String latitude = "55.92";
    private String longitude = "37.82";
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    public WeatherProperties(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String getCurrentWeatherUrl(){
        return String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s", latitude, longitude, key);
    }

    public String getDailyWeatherUrl(){
        return String.format("api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&appid=%s",latitude, longitude, key);
    }

    public void setCoordinates(String latitude, String longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        try {
            // Пересоздает все @RefreshScope бины
            ((ConfigurableApplicationContext) applicationContext).refresh();
        } catch (Exception e) {
            // Логирование
        }
    }
}
