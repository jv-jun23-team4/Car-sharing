package com.example.car.sharing.service;

import com.example.car.sharing.dto.rental.CreateRentalDto;
import com.example.car.sharing.dto.rental.RentalDto;
import java.time.LocalDate;
import java.util.List;

public interface RentalService {
    RentalDto create(CreateRentalDto rental);

    List<RentalDto> getByUserIdAndStatus(Long userId, Boolean isActive);

    RentalDto getById(Long id);

    List<RentalDto> getAll();

    void setActualReturnDate(Long id, LocalDate actualReturnDate);
}
