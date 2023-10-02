package com.example.car.sharing.dto.user;

import com.example.car.sharing.model.User;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private User.UserRole role;
}
