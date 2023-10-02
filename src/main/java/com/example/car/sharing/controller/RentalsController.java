package com.example.car.sharing.controller;

import com.example.car.sharing.model.Rental;
import com.example.car.sharing.service.RentalService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rentals")
public class RentalsController {
    private final RentalService rentalService;

    @PostMapping
    public void addRental(@RequestBody Rental rental) {
        rentalService.addRental(rental);
    }

    @GetMapping
    public List<Rental> getRentalsByUserIdAndStatus(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active", defaultValue = "true") Boolean isActive) {
        return rentalService.getRentalsByUserIdAndStatus(userId, isActive);
    }

    @GetMapping("/{id}")
    public Rental getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id);
    }

    @PostMapping("/{id}/return")
    public void setActualReturnDate(@PathVariable Long id,
                                    @RequestBody LocalDate actualReturnDate) {
        rentalService.setActualReturnDate(id, actualReturnDate);
    }
}
