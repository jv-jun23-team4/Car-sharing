package com.example.car.sharing.dto.car;

import com.example.car.sharing.model.Car;

public record CarSearchParameters(String[] brand, Car.CarType[] type,
                                  Integer fromPrice, Integer toPrice) {
}
