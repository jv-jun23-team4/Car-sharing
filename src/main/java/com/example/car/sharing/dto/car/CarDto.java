package com.example.car.sharing.dto.car;

import com.example.car.sharing.model.Car.CarType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private CarType type;
    private int inventory;
    private BigDecimal dailyFee;
}
