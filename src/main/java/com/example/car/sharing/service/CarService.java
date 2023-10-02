package com.example.car.sharing.service;

import com.example.car.sharing.dto.car.CarDto;
import com.example.car.sharing.dto.car.CreateCarDto;
import com.example.car.sharing.dto.car.UpdateCarDto;
import java.util.List;

public interface CarService {
    List<CarDto> findAll(int page);

    CarDto findById(Long id);

    CarDto create(CreateCarDto createCarDto);

    CarDto update(Long id, UpdateCarDto updateCarDto);

    void delete(Long id);
}
