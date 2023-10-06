package com.example.car.sharing.dto.payment;

import com.example.car.sharing.model.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record PaymentResponseDto(
        @JsonProperty("status") Payment.Status status,
        @JsonProperty("sessionUrl") String sessionUrl,
        @JsonProperty("sessionId") String sessionId,
        @JsonProperty("amountToPay") BigDecimal amountToPay
) {
}
