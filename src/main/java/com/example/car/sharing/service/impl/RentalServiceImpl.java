package com.example.car.sharing.service.impl;

import com.example.car.sharing.dto.rental.CreateRentalDto;
import com.example.car.sharing.dto.rental.RentalDto;
import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.mapper.RentalMapper;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.model.User;
import com.example.car.sharing.repository.CarRepository;
import com.example.car.sharing.repository.RentalRepository;
import com.example.car.sharing.service.NotificationService;
import com.example.car.sharing.service.RentalService;
import com.example.car.sharing.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private static final String NOTIFICATION_NEW_RENTAL = """
            Hello there!
            We are excited to inform you about your new rental details:
            Car Model: %s
            Return Date: %s
            Total Price: %s      
            Thank you for choosing our service!
            """;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserService userService;
    private final RentalMapper rentalMapper;
    private final NotificationService notificationService;

    public RentalDto addRental(CreateRentalDto createRentalDto) {
        Car car = carRepository.findById(createRentalDto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: "
                        + createRentalDto.getCarId()));
        User user = userService.getAuthenticatedUser();
        List<Rental> rentals = rentalRepository.findByUserId(user.getId());
        for (Rental currentRental : rentals) {
            if (currentRental.isActive()) {
                throw new EntityNotFoundException("You cannot rent more than one car at a time. "
                        + "Please complete the current rental transaction "
                        + "before initiating a new one.");
            }
        }
        if (car.getInventory() <= 0) {
            throw new EntityNotFoundException("Sorry this car is not available now.");
        }
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);
        Rental newRental = new Rental();
        newRental.setUserId(user.getId());
        newRental.setCarId(car.getId());
        newRental.setRentalDate(LocalDate.now());
        newRental.setReturnDate(createRentalDto.getReturnDate());
        sendNotificationOfNewRental(userService.getAuthenticatedUser(), newRental);
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

    private void sendNotificationOfNewRental(User user, Rental rental) {
        if (user.getChatId() == null) {
            return;
        }
        Car car = carRepository.findById(rental.getCarId()).get();
        String carName = car.getBrand() + " " + car.getModel();

        LocalDate returnDate = rental.getReturnDate();
        LocalDate rentalDate = rental.getRentalDate();

        long daysDifference = ChronoUnit.DAYS.between(rentalDate, returnDate);
        BigDecimal price = car.getDailyFee().multiply(BigDecimal.valueOf(daysDifference));
        notificationService.sendMessage(user.getChatId(),
                String.format(NOTIFICATION_NEW_RENTAL, carName, returnDate, price));
    }
}
