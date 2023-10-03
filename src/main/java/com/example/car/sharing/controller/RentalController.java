package com.example.car.sharing.controller;

import com.example.car.sharing.dto.rental.CreateRentalDto;
import com.example.car.sharing.dto.rental.RentalDto;
import com.example.car.sharing.model.Rental;
import com.example.car.sharing.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
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

@Tag(name = "Rental management", description = "Endpoints for managing users' car rentals")
@RequiredArgsConstructor
@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @Operation(summary = "Add rental",
            description = "Add a new rental and decrease car inventory by 1")
    public RentalDto addRental(@RequestBody CreateRentalDto rental) {
        return rentalService.addRental(rental);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping
    @Operation(summary = "Get rentals by user ID and its status",
            description = "Get rentals by user ID and whether the rental is still active or not")
    public List<Rental> getRentalsByUserIdAndStatus(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active", defaultValue = "true") Boolean isActive) {
        return rentalService.getRentalsByUserIdAndStatus(userId, isActive);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/{id}")
    @Operation(summary = "Get specific rental", description = "Get rental by ID")
    public Rental getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("/{id}/return")
    @Operation(summary = "Set actual return date",
            description = "Set actual return date (increase car inventory by 1)")
    public void setActualReturnDate(@PathVariable Long id,
                                    @RequestBody LocalDate actualReturnDate) {
        rentalService.setActualReturnDate(id, actualReturnDate);
    }
}
