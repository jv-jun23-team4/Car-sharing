package com.example.car.sharing.dto.payment;

import com.example.car.sharing.model.Payment.Type;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    @NotNull
    @Positive
    private Long rentalId;

    @NotNull
    private Type type;
}
