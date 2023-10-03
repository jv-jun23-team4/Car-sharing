package com.example.car.sharing.controller;

import com.example.car.sharing.dto.car.CarDto;
import com.example.car.sharing.dto.car.CreateCarDto;
import com.example.car.sharing.dto.car.UpdateCarDto;
import com.example.car.sharing.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(summary = "Get a car", description = "Get car by ID")
    public CarDto getById(@PathVariable Long id) {
        return carService.findById(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    @Operation(summary = "Create a car", description = "Create a new car")
    public CarDto create(@RequestBody @Valid CreateCarDto createCarDto) {
        return carService.create(createCarDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a car", description = "Update data about an existing car")
    public CarDto update(@PathVariable Long id, @RequestBody UpdateCarDto updateDto) {
        return carService.update(id, updateDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a car", description = "Delete car by ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        carService.delete(id);
    }
}
