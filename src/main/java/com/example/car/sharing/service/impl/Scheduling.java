package com.example.car.sharing.service.impl;

import com.example.car.sharing.model.Payment;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.repository.PaymentRepository;
import com.example.car.sharing.repository.RentalRepository;
import com.example.car.sharing.repository.UserRepository;
import com.example.car.sharing.service.NotificationService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Scheduling {
    private static final String LAST_RENTAL_DAY_MESSAGE = """
            Hello.
            Today is the last day of car rental. If you miss the return date, an additional fee
            (dailyFee * 30%) will be charged to your account for each subsequent day.
            Return the cars on time :)
            """;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 360000)
    public void checkPaymentExpiry() {
        Instant currentTime = Instant.now();
        List<Payment> expiredPayments = paymentRepository.findByStatusAndExpiredTimeAfter(
                Payment.Status.PENDING, currentTime
        );

        for (Payment payment : expiredPayments) {
            payment.setStatus(Payment.Status.EXPIRED);
            updatePayment(payment);
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
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

    private void updatePayment(Payment payment) {
        paymentRepository.save(payment);
    }
}
