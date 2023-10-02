package com.example.car.sharing.dto.user;

public record UserRegistrationResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}
