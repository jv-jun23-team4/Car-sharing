package com.example.car.sharing.service.payment;

import com.example.car.sharing.exception.EntityNotFoundException;
import com.example.car.sharing.model.Car;
import com.example.car.sharing.model.Payment;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.repository.CarRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CalculateTotalPrice {
    private static final BigDecimal FINE_PERCENTAGE = BigDecimal.valueOf(1.3);
    private final CarRepository carRepository;

    public final BigDecimal calculate(Payment payment) {
        Car car = carRepository.findById(payment.getRental().getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find a car with id"
                        + payment.getRental().getCarId())
        );
        if (payment.getType().equals(Payment.Type.FINE)) {
            return calculateForFine(payment.getRental(), car);
        }
        return calculateForPayment(payment.getRental(), car);
    }

    private static BigDecimal calculateForPayment(Rental rental, Car car) {
        LocalDate returnDate = rental.getReturnDate();
        LocalDate rentalDate = rental.getRentalDate();

        long daysDifference = ChronoUnit.DAYS.between(rentalDate, returnDate);
        return BigDecimal.valueOf(daysDifference).multiply(car.getDailyFee());
    }

    private static BigDecimal calculateForFine(Rental rental, Car car) {
        if (rental.getActualReturnDate() == null) {
            throw new EntityNotFoundException("This car is not return yet");
        }
        LocalDate returnDate = rental.getReturnDate();
        LocalDate actualReturnDate = rental.getActualReturnDate();

        long daysDifference = ChronoUnit.DAYS.between(returnDate, actualReturnDate);
        BigDecimal beforeReturnDate = calculateForPayment(rental, car);
        BigDecimal afterReturnDate = BigDecimal.valueOf(daysDifference)
                .multiply(car.getDailyFee().multiply(FINE_PERCENTAGE));

        return beforeReturnDate.add(afterReturnDate);
    }
}
