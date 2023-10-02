package com.example.car.sharing.service;

import com.example.car.sharing.dto.rental.CreateRentalDto;
import com.example.car.sharing.dto.rental.RentalDto;
import com.example.car.sharing.model.Rental;
import java.time.LocalDate;
import java.util.List;

public interface RentalService {
    RentalDto addRental(CreateRentalDto rental);

    List<Rental> getRentalsByUserIdAndStatus(Long userId, Boolean isActive);

    Rental getRentalById(Long id);

    void setActualReturnDate(Long id, LocalDate actualReturnDate);
}
