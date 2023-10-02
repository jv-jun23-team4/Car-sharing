package com.example.car.sharing.service;

import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import com.example.car.sharing.dto.user.UserRegistrationResponseDto;
import com.example.car.sharing.exception.RegistrationException;
import com.example.car.sharing.model.User;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto userRequestDto)
            throws RegistrationException;

    User getAuthenticatedUser();
}
