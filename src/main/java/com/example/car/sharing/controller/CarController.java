package com.example.car.sharing.controller;

import com.example.car.sharing.dto.car.CarCreateDto;
import com.example.car.sharing.dto.car.CarDto;
import com.example.car.sharing.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Car management", description = "Endpoints for managing cars")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @GetMapping
    @Operation(summary = "Get all cars", description = "Get list of all available cars")
    public List<CarDto> getAll(@RequestParam(defaultValue = "0") int page) {
        return carService.findAll(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get car by id", description = "Get car by id")
    public CarDto getById(@PathVariable Long id) {
        return carService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new car", description = "Create a new car")
    public CarDto create(@RequestBody @Valid CarCreateDto carCreateDto) {
        return carService.create(carCreateDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a car", description = "Update data about an existing car")
    public CarDto update(@PathVariable Long id, @RequestBody CarDto carDto) {
        return carService.update(id, carDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete car by id", description = "Delete car by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        carService.delete(id);
    }
}
