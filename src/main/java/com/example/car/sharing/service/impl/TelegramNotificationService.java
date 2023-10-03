package com.example.car.sharing.service.impl;

import com.example.car.sharing.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot telegramBot;

    @Override
    public void sendMessage(long chatId, String message) {
        telegramBot.sendMessage(chatId, message);
    }
}
