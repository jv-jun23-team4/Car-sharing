package com.example.car.sharing.dto.payment;

import com.example.car.sharing.model.Payment;
import java.math.BigDecimal;

public record PaymentResponseDto(
        Payment.Status status,
        String sessionUrl,
        String sessionId,
        BigDecimal amountToPay
) {
}
