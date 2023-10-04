package com.example.car.sharing.dto.car;

import com.example.car.sharing.model.Car.CarType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCarDto {
    @Size(min = 1, max = 50)
    @NotBlank
    private String model;

    @Size(min = 1, max = 50)
    @NotBlank
    private String brand;

    private CarType type;

    @NotNull
    @Positive
    private int inventory;

    @NotNull
    @Positive
    private BigDecimal dailyFee;
}
