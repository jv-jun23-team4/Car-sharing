package com.example.car.sharing.service.impl;

import com.example.car.sharing.config.BotConfig;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.model.User;
import com.example.car.sharing.repository.CarRepository;
import com.example.car.sharing.repository.RentalRepository;
import com.example.car.sharing.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private static final String WELCOME_MESSAGE = """
            . Welcome to the Car Sharing Bot.
            With this bot, you will be able to conveniently manage your rentals
            and receive notifications about the status of your rental.
            """;
    private static final String RENTAL_TEMPLATE = """
        Rental Details:
        Car Model: %s
        Rental Date: %s
        Return Date: %s
        Total Price: %s
            """;
    private static final String CHOOSE_OPTION_MESSAGE =
            "Please, choose what you want to see";
    private static final String START_COMMAND = "/start";
    private static final String REGISTER_COMMAND = "/register";
    private static final String MY_RENTALS = "/my_rentals";
    private static final String MY_HISTORY = "/my_history";
    private static final String RENTALS_MENU = "/rentals_menu";
    private static final String WRONG_PASSWORD_MESSAGE =
            "Wrong email or password, please try again";
    private static final String INCORRECT_REQUEST =
            "Incorrect request. Please, write your message according to the pattern: \n"
                    + "<email> <password>";
    private static final String TIP_ABOUT_REGISTRATION_PATTERN =
            "Write your message according to the pattern: \n"
                    + "<email> <password>";
    private static final String UNREGISTERED_MESSAGE =
            "You are not registered. Please register to access your rental history.";
    private static final String SPACE_SPLITTER = " ";
    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;
    private final BotConfig botConfig;
    private final PasswordEncoder passwordEncoder;
    private List<BotCommand> commands;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;

    @PostConstruct
    private void initMenu() {
        commands = new ArrayList<>();
        commands.add(new BotCommand(START_COMMAND, "You can start work with bot"));
        commands.add(new BotCommand(REGISTER_COMMAND, "You can authenticate yourself"));
        commands.add(new BotCommand(MY_RENTALS, "You can see all rental's history"));
        commands.add(new BotCommand(MY_HISTORY, "You can see your current rental"));
        commands.add(new BotCommand(RENTALS_MENU, "You can choose option by button"));
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't initialize command menu", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            long buttonChatId = update.getCallbackQuery().getMessage().getChatId();
            String callBackData = update.getCallbackQuery().getData();
            if (callBackData.equals(MY_RENTALS)) {
                sendCurrentRental(buttonChatId);
            } else {
                sendRentalHistory(buttonChatId);
            }
        }
        long chatId = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            switch (message) {
                case START_COMMAND -> startCommandReceived(chatId, getName(update));
                case REGISTER_COMMAND -> sendMessage(chatId, TIP_ABOUT_REGISTRATION_PATTERN);
                case MY_RENTALS -> sendCurrentRental(chatId);
                case MY_HISTORY -> sendRentalHistory(chatId);
                case RENTALS_MENU -> sendKeyboard(chatId, CHOOSE_OPTION_MESSAGE);
                default -> registerCommandReceived(chatId, update);
            }
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        executeMessage(message);
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
                && passwordEncoder.matches(
                        userData[PASSWORD_INDEX],
                        optionalUser.get().getPassword())) {

            User user = optionalUser.get();
            user.setChatId(chatId);
            userRepository.save(user);
        } else {
            sendMessage(chatId, WRONG_PASSWORD_MESSAGE);
        }
    }

    private void sendRentalHistory(long chatId) {
        Optional<User> user = userRepository.findByChatId(chatId);
        if (user.isPresent()) {
            List<Rental> rentals = rentalRepository.findByUserId(user.get().getId());
            sendRentalDetailsMessage(chatId, rentals);
        } else {
            sendMessage(chatId, UNREGISTERED_MESSAGE);
        }
    }

    private void sendCurrentRental(long chatId) {
        Optional<User> user = userRepository.findByChatId(chatId);
        if (user.isPresent()) {
            List<Rental> rentals = rentalRepository
                    .findByUserIdAndIsActive(user.get().getId(), true);
            sendRentalDetailsMessage(chatId, rentals);
        } else {
            sendMessage(chatId, UNREGISTERED_MESSAGE);
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't send message. PLease, try again" + e);
        }
    }

    private String getName(Update update) {
        return update.getMessage().getChat().getFirstName();
    }

    private String getRentalPrice(Car car, Rental rental) {
        return String.valueOf(car.getDailyFee().multiply(
                BigDecimal.valueOf(ChronoUnit.DAYS.between(
                                rental.getRentalDate(), rental.getReturnDate()))));
    }

    private void sendRentalDetailsMessage(long chatId, List<Rental> rentals) {
        List<String> messages = new ArrayList<>();
        for (Rental rental: rentals) {
            Car car = carRepository.findById(rental.getCarId()).orElseThrow(
                    () -> new EntityNotFoundException("Can't find a car with id "
                            + rental.getCarId())
            );
            String price = getRentalPrice(car, rental);
            messages.add(String.format(RENTAL_TEMPLATE,
                    car.getModel(), rental.getRentalDate(), rental.getReturnDate(), price));
            messages.add(System.lineSeparator());
        }
        String currentRentals = String.join(System.lineSeparator(), messages);
        if (currentRentals.isEmpty()) {
            sendMessage(chatId, "You don't have rentals yet");
        } else {
            sendMessage(chatId, currentRentals);
        }
    }

    private void sendKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        InlineKeyboardButton myHistoryButton = new InlineKeyboardButton();
        myHistoryButton.setText("My history");
        myHistoryButton.setCallbackData(MY_HISTORY);

        InlineKeyboardButton myRentalsButton = new InlineKeyboardButton();
        myRentalsButton.setText("My rentals");
        myRentalsButton.setCallbackData(MY_RENTALS);

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        rowInLine.add(myHistoryButton);
        rowInLine.add(myRentalsButton);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(rowInLine));
        message.setReplyMarkup(markup);
        executeMessage(message);
    }
}
