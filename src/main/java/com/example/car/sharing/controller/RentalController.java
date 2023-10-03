package com.example.car.sharing.controller;

import com.example.car.sharing.dto.rental.CreateRentalDto;
import com.example.car.sharing.dto.rental.RentalDto;
import com.example.car.sharing.dto.rental.SetActualReturnDateDto;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.service.RentalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    public RentalDto addRental(@RequestBody CreateRentalDto rental) {
        return rentalService.addRental(rental);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping
    public List<Rental> getRentalsByUserIdAndStatus(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active", defaultValue = "true") Boolean isActive) {
        return rentalService.getRentalsByUserIdAndStatus(userId, isActive);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/{id}")
    public Rental getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/{id}/return")
    public void setActualReturnDate(@PathVariable Long id,
                                    @RequestBody SetActualReturnDateDto actualReturnDate) {
        rentalService.setActualReturnDate(id, actualReturnDate.actualReturnDate());
    }
}
