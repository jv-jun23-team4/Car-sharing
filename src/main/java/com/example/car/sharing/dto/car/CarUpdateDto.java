package com.example.car.sharing.dto.car;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarUpdateDto {
    @NotNull
    @Positive
    private int inventory;

    @NotNull
    @Positive
    private BigDecimal dailyFee;
}
