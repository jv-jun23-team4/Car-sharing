package com.example.car.sharing.dto.payment;

import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long rentalId;
    private URL sessionUrl;
    private BigDecimal amount;
}
