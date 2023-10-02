package com.example.car.sharing.dto.user;

import com.example.car.sharing.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldMatch(field = "password", fieldMatch = "repeatPassword")
public class UserRegistrationRequestDto {
    @NotNull
    @Email
    private String email;
    @NotNull
    @NotBlank
    @Size(min = 8, max = 30)
    private String password;
    @NotNull
    @NotBlank
    @Size(min = 8, max = 30)
    private String repeatPassword;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    private String firstName;
    @NotNull
    @NotBlank
    @Size(min = 0, max = 255)
    private String lastName;
}
