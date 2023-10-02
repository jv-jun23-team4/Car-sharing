package com.example.car.sharing.service;

import com.example.car.sharing.dto.user.UserDto;
import com.example.car.sharing.model.User;

public interface UserService {
    void updateUserRoleById(Long id, User.UserRole role);
    UserDto getUserById(Long id);
    UserDto update(Long id, UserDto userDto);
}
