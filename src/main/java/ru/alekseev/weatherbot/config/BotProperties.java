package ru.alekseev.weatherbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot") // тот самый префикс
@Data // lombok
@PropertySource("classpath:application.yml")
public class BotProperties {

    private String name;

    private String token;

}
