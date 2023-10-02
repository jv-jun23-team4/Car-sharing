package com.example.car.sharing.dto.rental;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRentalDto {
    private Long carId;
    private LocalDate returnDate;
}
