package ru.alekseev.weatherbot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer{

    private static final String START_MESSAGE = "Привет, я - бот погоды.\n" +
            "Пока что я показываю актуальную погоду в Королёве.\n" +
            "Выберете действие: ";
    private static final String MESSAGE_ERROR = "Неправильная команда";

    private final TelegramClient telegramClient;
    private final BotProperties botProperties;
    private final WeatherProperties weatherProperties;
    private final WeatherService weatherService;
    private final ScheduledService scheduledService;

    @Autowired
    public UpdateConsumer(BotProperties botProperties,
                          WeatherProperties weatherProperties,
                          WeatherService weatherService,
                          ScheduledService scheduledService) {
        this.botProperties = botProperties;
        this.telegramClient = new OkHttpTelegramClient(botProperties.getToken());
        this.weatherProperties = weatherProperties;
        this.weatherService = weatherService;
        this.scheduledService = scheduledService;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (checkCommand(update.getMessage().getText())) {
                handlerCommand(update.getMessage().getText(),
                        update.getMessage().getChatId());
            } else {
                sendMessage(MESSAGE_ERROR, update.getMessage().getChatId());
            }
        }
    }

    private boolean checkCommand(String command) {
        return command.startsWith("/") || command.equals("Текущая погода");
    }

    private void handlerCommand(String command, Long chatId) {
        switch (command) {
            case "/start":
                sendMainMenu(START_MESSAGE + "\n" + scheduledService.addChat(chatId), chatId);
                //scheduledService.addChat(chatId);
                break;
            case "/help":
                break;
            case "Текущая погода":
                sendCurrentWeather(chatId);
            default:
                break;
        }
    }

    private void sendMainMenu(String message, Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .build();

//            var button1 = InlineKeyboardButton.builder()
//                    .text("Погода")
//                    .callbackData("/currentWeather")
//                    .build();

            List<KeyboardRow> rows = List.of(
                    new KeyboardRow("Текущая погода")
            );

            //List<InlineKeyboardRow> buttons = List.of(new ReplyKeyboardRow(button1));

            //InlineKeyboardMarkup markup = new InlineKeyboardMarkup(buttons);
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rows);
            sendMessage.setReplyMarkup(markup);

            telegramClient.execute(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }

    }

// Не актуально!!
//    private void handlerCallbackQuery(CallbackQuery callbackQuery) {
//        var data = callbackQuery.getData();
//        switch (data){
//            case "weather":
//                sendWeather(callbackQuery.getMessage().getChatId());
//                break;
//        }
//
//    }

    private void sendCurrentWeather(Long chatId) {
        String data = weatherService.getCurrentWeatherResponse();
        sendMessage(data, chatId);
    }

    private void sendMessage (String message, Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .build();
            telegramClient.execute(sendMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }

    }

}
