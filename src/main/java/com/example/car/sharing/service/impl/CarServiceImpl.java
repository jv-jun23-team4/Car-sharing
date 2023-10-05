package com.example.car.sharing.service.impl;

import com.example.car.sharing.dto.car.CarDto;
import com.example.car.sharing.dto.car.CarSearchParameters;
import com.example.car.sharing.dto.car.CreateCarDto;
import com.example.car.sharing.dto.car.UpdateCarDto;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.mapper.CarMapper;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.repository.car.CarRepository;
import com.example.car.sharing.repository.car.CarSpecificationBuilder;
import com.example.car.sharing.service.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private static final int PAGE_SIZE = 20;
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final CarSpecificationBuilder specificationBuilder;

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
    public CarDto create(CreateCarDto createCarDto) {
        Car car = carMapper.toEntity(createCarDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Transactional
    @Override
    public CarDto update(Long id, UpdateCarDto updateCarDto) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can`t find car by id: " + id));
        existingCar.setInventory(updateCarDto.getInventory());
        existingCar.setDailyFee(updateCarDto.getDailyFee());
        return carMapper.toDto(carRepository.save(existingCar));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Can`t find car by id: " + id);
        }
        carRepository.deleteById(id);
    }

    @Override
    public List<CarDto> findByParams(CarSearchParameters params) {
        Specification<Car> carSpecification = specificationBuilder.build(params);
        List<Car> cars = carRepository.findAll(
                (root, query, criteriaBuilder) -> {
                    query.distinct(true);
                    return carSpecification.toPredicate(root, query, criteriaBuilder);
                });

        return cars.stream()
                .map(carMapper::toDto)
                .toList();
    }
}
