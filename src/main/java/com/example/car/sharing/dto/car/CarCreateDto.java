package com.example.car.sharing.dto.car;

import com.example.car.sharing.model.Car.CarType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarCreateDto {
    @Size(min = 1, max = 50)
    @NotBlank
    private String model;

    @Size(min = 1, max = 50)
    @NotBlank
    private String brand;

    @NotBlank
    private CarType type;

    @NotNull
    @Positive
    private int inventory;

    @NotNull
    @Positive
    private BigDecimal dailyFee;
}
