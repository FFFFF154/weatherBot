package ru.alekseev.weatherbot.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.alekseev.weatherbot.config.BotProperties;
import ru.alekseev.weatherbot.config.WeatherProperties;
import ru.alekseev.weatherbot.service.ScheduledService;
import ru.alekseev.weatherbot.service.WeatherService;

import java.util.List;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private static final String START_MESSAGE = "Привет, я - бот погоды.\n" +
            "Пока что я показываю актуальную погоду в Королёве.\n" +
            "Выберете действие: ";
    private static final String MESSAGE_ERROR = "Неправильная команда";
    private static final String CHANGE_CITY_MESSAGE = "Выберете город";
    private static final String SET_CITY_MESSAGE = "Город изменен\nТекущий город: ";

    private static final List<KeyboardRow> ROWS_MAIN = List.of(
            new KeyboardRow("Текущая погода"),
            new KeyboardRow("Выбор города")
    );

    private static final String KOROLEV = "Королёв";
    private static final String MOSCOW = "Москва";
    private static final String IZHEVSK = "Ижевск";

    private static final List<String> HOT_WORDS = List.of("/",
            "Текущая погода",
            "Выбор города",
            "Королёв",
            "Москва",
            "Ижевск");

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
            } else if (isHotWord(update.getMessage().getText())) {
                handlerHotWord(update.getMessage().getText(),
                        update.getMessage().getChatId());
            } else {
                sendMessage(MESSAGE_ERROR, update.getMessage().getChatId());
            }
        }
    }

    private boolean checkCommand(String command) {
        return command.startsWith("/");
    }

    private boolean isHotWord(String command) {
        return HOT_WORDS.contains(command);
    }

    private void handlerCommand(String command, Long chatId) {
        switch (command) {
            case "/start":
                sendMainMenu(START_MESSAGE + "\n" + scheduledService.addChat(chatId), chatId);
                //scheduledService.addChat(chatId);
                break;
            case "/help":
                break;

        }
    }

    private void handlerHotWord(String hotWord, Long chatId) {
        switch (hotWord) {
            case "Текущая погода":
                sendCurrentWeather(chatId);
                break;
            case "Выбор города":
                changeCity(chatId);
                break;
            case KOROLEV:
                weatherProperties.setCoordinates("55.92", "37.82");
                setCity(chatId, KOROLEV);
                break;
            case MOSCOW:
                weatherProperties.setCoordinates("55.75", "37.62");
                setCity(chatId, MOSCOW);
                break;
            case IZHEVSK:
                weatherProperties.setCoordinates("56.85", "53.2");
                setCity(chatId, IZHEVSK);
                break;
            default:
                break;
        }
    }

    private void setCity(Long chatId, String city) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .text(SET_CITY_MESSAGE + city)
                    .chatId(chatId)
                    .build();

            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(ROWS_MAIN);
            sendMessage.setReplyMarkup(markup);

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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


            //List<InlineKeyboardRow> buttons = List.of(new ReplyKeyboardRow(button1));

            //InlineKeyboardMarkup markup = new InlineKeyboardMarkup(buttons);
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(ROWS_MAIN);
            sendMessage.setReplyMarkup(markup);

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
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

    private void sendMessage(String message, Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .text(message)
                    .chatId(chatId)
                    .build();
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void changeCity(Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .text(CHANGE_CITY_MESSAGE)
                    .chatId(chatId)
                    .build();

            List<KeyboardRow> rows = List.of(
                    new KeyboardRow("Королёв"),
                    new KeyboardRow("Москва"),
                    new KeyboardRow("Ижевск")
            );

            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rows);
            sendMessage.setReplyMarkup(markup);

            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
