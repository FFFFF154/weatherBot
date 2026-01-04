package ru.alekseev.weatherbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ScheduledService {

    private final TelegramClient telegramClient;
    private final BotProperties botProperties;
    private final WeatherService weatherService;

    private final Set<Long> chats = ConcurrentHashMap.newKeySet();

    @Autowired
    public ScheduledService(WeatherService weatherService,
                            BotProperties botProperties) {
        this.botProperties = botProperties;
        this.telegramClient = new OkHttpTelegramClient(botProperties.getToken());
        this.weatherService = weatherService;
    }


    @Scheduled(cron = "0 0 7 * * ?", zone = "Europe/Moscow")
    public void sendDailyWeather() {
        String dailyWeather = weatherService.getDailyWeatherResponse();
        chats.forEach(chat -> {
            try {
                sendMessage(dailyWeather, chat);
            } catch (Exception e){
                log.error(e.getMessage());
            }
        });
    }

    public boolean addChat(Long chatId) {
        if (chats.contains(chatId)) {
            return false;
        } else{
            chats.add(chatId);
            return true;
        }
    }

    private void sendMessage(String message, Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .build();
            telegramClient.execute(sendMessage);
        }catch (TelegramApiException e){
            log.error(e.getMessage());
        }

    }
}
