package com.example.car.sharing.service.impl;

import com.example.car.sharing.model.Rental;
import com.example.car.sharing.repository.rental.RentalRepository;
import com.example.car.sharing.repository.user.UserRepository;
import com.example.car.sharing.service.NotificationService;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class SchedulingService {
    private static final String LAST_RENTAL_DAY_MESSAGE = """
            Hello.
            Today is the last day of car rental. If you miss the return date, an additional fee
            (dailyFee * 30%) will be charged to your account for each subsequent day.
            Return the cars on time :)
            """;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional(readOnly = true)
    public void sendRentalWillEndSoon() {
        Set<Long> userIds = rentalRepository.findAll().stream()
                .filter(Rental::isActive)
                .filter(r -> r.getReturnDate().isEqual(LocalDate.now()))
                .map(Rental::getUserId)
                .collect(Collectors.toSet());
        for (Long id : userIds) {
            Long chatId = userRepository.findById(id).get().getChatId();
            notificationService.sendMessage(chatId, LAST_RENTAL_DAY_MESSAGE);
        }
    }
}
