package com.example.car.sharing.service.impl;

import com.example.car.sharing.model.Payment;
import com.example.car.sharing.repository.PaymentRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DatabaseUtil {
    private final PaymentRepository paymentRepository;

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

    private void updatePayment(Payment payment) {
        paymentRepository.save(payment);
    }
}
