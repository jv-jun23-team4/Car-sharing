package com.example.car.sharing.service.impl;

import com.example.car.sharing.dto.rental.CreateRentalDto;
import com.example.car.sharing.dto.rental.RentalDto;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.mapper.RentalMapper;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.repository.CarRepository;
import com.example.car.sharing.repository.RentalRepository;
import com.example.car.sharing.service.RentalService;
import com.example.car.sharing.service.UserService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserService userService;
    private final RentalMapper rentalMapper;

    public RentalDto addRental(CreateRentalDto createRentalDto) {
        Car car = carRepository.findById(createRentalDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: "
                        + createRentalDto.getCarId()));
        carRepository.save(car);
        Rental newRental = new Rental();
        newRental.setUserId(userService.getAuthenticatedUser().getId());
        newRental.setCarId(car.getId());
        newRental.setRentalDate(LocalDate.now());
        newRental.setReturnDate(createRentalDto.getReturnDate());
        car.setInventory(car.getInventory() - 1);
        return rentalMapper.toDto(rentalRepository.save(newRental));
    }

    public List<Rental> getRentalsByUserIdAndStatus(Long userId, Boolean isActive) {
        return rentalRepository.findByUserIdAndIsActive(userId, isActive);
    }

    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id: " + id));
    }

    public void setActualReturnDate(Long id, LocalDate actualReturnDate) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id: " + id));
        rental.setActualReturnDate(actualReturnDate);
        rentalRepository.save(rental);
        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: "
                        + rental.getCarId()));
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
    }
}
