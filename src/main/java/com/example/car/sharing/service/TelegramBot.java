package com.example.car.sharing.service;

import com.example.car.sharing.config.BotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private static final String WELCOME_MESSAGE = """
            . Welcome to the Car Sharing Bot.
            With this bot, you will be able to conveniently manage your rentals
            and receive notifications about the status of your rental.
            """;
    private static final String COMMAND_NOT_FOUND_MESSAGE = "Sorry, command not found";
    private final BotConfig botConfig;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (message.equals("/start")) {
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            } else {
                sendMessage(chatId, COMMAND_NOT_FOUND_MESSAGE);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + WELCOME_MESSAGE;
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't send message. PLease, try again" + e);
        }
    }
}
