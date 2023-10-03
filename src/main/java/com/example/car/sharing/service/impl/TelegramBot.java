package com.example.car.sharing.service.impl;

import com.example.car.sharing.config.BotConfig;
import com.example.car.sharing.model.User;
import com.example.car.sharing.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
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
    private static final String REGISTER_COMMAND = "/register";
    private static final String NEW_RENTAL_COMMAND = "/newrental";
    private static final String END_RENTAL_COMMAND = "/endrental";
    private static final String WRONG_PASSWORD_MESSAGE =
            "Wrong email or password, please try again";
    private static final String INCORRECT_REQUEST =
            "Incorrect request. Please, write your message according to the pattern: \n"
                    + "<email> <password>";
    private static final String TIP_ABOUT_REGISTRATION_PATTERN =
            "Write your message according to the pattern: \n"
                    + "<email> <password>";
    private static final String SPACE_SPLITTER = " ";
    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;
    private final BotConfig botConfig;
    private final PasswordEncoder passwordEncoder;
    private List<BotCommand> commands;
    private final UserRepository userRepository;

    @PostConstruct
    private void initMenu() {
        commands = new ArrayList<>();
        commands.add(new BotCommand(START_COMMAND, "send a welcome message"));
        commands.add(new BotCommand(REGISTER_COMMAND, "authenticate user by email and password"));
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
                case REGISTER_COMMAND -> sendMessage(chatId, TIP_ABOUT_REGISTRATION_PATTERN);
                case NEW_RENTAL_COMMAND -> newRentalCommandReceived(chatId, getName(update));
                case END_RENTAL_COMMAND -> endRentalCommandReceived(chatId, getName(update));
                default -> registerCommandReceived(chatId, update);
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

    private void registerCommandReceived(long chatId, Update update) {
        Message message = update.getMessage();
        String[] userData = message.getText().split(SPACE_SPLITTER);
        if (userData.length != 2) {
            sendMessage(chatId, INCORRECT_REQUEST);
            return;
        }
        Optional<User> optionalUser =
                userRepository.findByEmail(userData[EMAIL_INDEX]);
        if (optionalUser.isPresent()
                && optionalUser.get().getPassword()
                .equals(passwordEncoder.encode(userData[PASSWORD_INDEX]))) {
            User user = optionalUser.get();
            user.setChatId(chatId);
            userRepository.save(user);
        } else {
            sendMessage(chatId, WRONG_PASSWORD_MESSAGE);
        }
    }

    private void newRentalCommandReceived(long chatId, String name) {
        String answer = name + RENTAL_COMPLETED_MESSAGE;
        sendMessage(chatId, answer);
    }

    private void endRentalCommandReceived(long chatId, String name) {
        String answer = name + RENTAL_ENDED_MESSAGE;
        sendMessage(chatId, answer);
    }

    public void sendMessage(long chatId, String text) {
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
