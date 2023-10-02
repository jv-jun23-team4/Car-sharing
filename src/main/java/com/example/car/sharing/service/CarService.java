package com.example.car.sharing.service;

import com.example.car.sharing.dto.car.CarCreateDto;
import com.example.car.sharing.dto.car.CarDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    List<CarDto> findAll(int page);

    CarDto findById(Long id);

    CarDto create(CarCreateDto carCreateDto);

    CarDto update(Long id, CarDto carDto);

    void delete(Long id);
}
