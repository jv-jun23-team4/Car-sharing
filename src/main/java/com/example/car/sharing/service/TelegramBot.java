package com.example.car.sharing.service;

import com.example.car.sharing.config.BotConfig;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private static final String WELCOME_MESSAGE = """
            . Welcome to the Car Sharing Bot.
            With this bot, you will be able to conveniently manage your rentals
            and receive notifications about the status of your rental.
            """;
    private static final String RENTAL_COMPLETED_MESSAGE =
            ", your rental successfully completed.";
    private static final String RENTAL_ENDED_MESSAGE =
            ", your rental successfully closed.";
    private static final String START_COMMAND = "/start";
    private static final String NEW_RENTAL_COMMAND = "/newrental";
    private static final String END_RENTAL_COMMAND = "/endrental";
    private static final String COMMAND_NOT_FOUND_MESSAGE = "Sorry, command not found";
    private final BotConfig botConfig;
    private List<BotCommand> commands;

    @PostConstruct
    private void initMenu() {
        commands = new ArrayList<>();
        commands.add(new BotCommand(START_COMMAND, "send a welcome message"));
        commands.add(new BotCommand(NEW_RENTAL_COMMAND, "add a new rental"));
        commands.add(new BotCommand(END_RENTAL_COMMAND, "end current rental"));
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't initialize command menu", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (message) {
                case START_COMMAND -> startCommandReceived(chatId, getName(update));
                case NEW_RENTAL_COMMAND -> newRentalCommandReceived(chatId, getName(update));
                case END_RENTAL_COMMAND -> endRentalCommandReceived(chatId, getName(update));
                default -> sendMessage(chatId, COMMAND_NOT_FOUND_MESSAGE);
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

    private void newRentalCommandReceived(long chatId, String name) {
        String answer = name + RENTAL_COMPLETED_MESSAGE;
        sendMessage(chatId, answer);
    }

    private void endRentalCommandReceived(long chatId, String name) {
        String answer = name + RENTAL_ENDED_MESSAGE;
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

    private String getName(Update update) {
        return update.getMessage().getChat().getFirstName();
    }
}
