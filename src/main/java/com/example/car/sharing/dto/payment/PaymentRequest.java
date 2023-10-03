package com.example.car.sharing.dto.payment;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long rentalId;
    private BigDecimal amount;
}
