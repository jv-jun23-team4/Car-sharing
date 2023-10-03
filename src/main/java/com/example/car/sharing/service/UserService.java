package com.example.car.sharing.service;

import com.example.car.sharing.dto.user.UpdateUserData;
import com.example.car.sharing.dto.user.UserDto;
import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import com.example.car.sharing.dto.user.UserRegistrationResponseDto;
import com.example.car.sharing.exception.RegistrationException;
import com.example.car.sharing.model.User;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto userRequestDto)
            throws RegistrationException;

    User getAuthenticatedUser();
  
    UserDto updateUserRoleById(Long id, User.UserRole role);
  
    UserDto getUserById();
  
    UserDto update(UpdateUserData updateUserData);
}
