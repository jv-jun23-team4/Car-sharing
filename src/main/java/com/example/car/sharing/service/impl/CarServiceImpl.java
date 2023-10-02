package com.example.car.sharing.service.impl;

import com.example.car.sharing.dto.car.CarCreateDto;
import com.example.car.sharing.dto.car.CarDto;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.mapper.CarMapper;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.repository.CarRepository;
import com.example.car.sharing.service.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private static final int PAGE_SIZE = 50;
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public List<CarDto> findAll(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return carRepository.findAll(pageable)
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto findById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can`t find car by id: " + id));
        return carMapper.toDto(car);
    }

    @Override
    public CarDto create(CarCreateDto carCreateDto) {
        Car car = carMapper.toEntity(carCreateDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public CarDto update(Long id, CarDto carDto) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can`t find car by id: " + id));
        Car updatedCar = carMapper.toEntity(carDto);
        updatedCar.setId(existingCar.getId());
        return carMapper.toDto(carRepository.save(updatedCar));
    }

    @Override
    public void delete(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Can`t find car by id: " + id);
        }
        carRepository.deleteById(id);
    }
}
