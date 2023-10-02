package com.example.car.sharing.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateUserData {
    @NotNull
    @Length(min = 5, max = 50)
    @Email
    private String email;
    @NotNull
    @Length(min = 2, max = 50)
    private String firstName;
    @NotNull
    @Length(min = 2, max = 50)
    private String lastName;
}
