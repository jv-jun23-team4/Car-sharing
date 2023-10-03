package com.example.car.sharing.dto.payment;

import com.example.car.sharing.model.Payment.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long rentalId;
    private Type type;
}
