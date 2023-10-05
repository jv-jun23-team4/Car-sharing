package com.example.car.sharing.dto.user;

import com.example.car.sharing.model.User;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateUserRole {
    private User.UserRole role;
}
