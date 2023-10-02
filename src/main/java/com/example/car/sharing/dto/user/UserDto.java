package com.example.car.sharing.dto.user;

import com.example.car.sharing.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserDto {
    private Long id;
    @NotEmpty
    @Length(min = 5, max = 50)
    @Email
    private String email;
    @NotEmpty
    @Length(min = 2, max = 50)
    private String firstName;
    @NotEmpty
    @Length(min = 2, max = 50)
    private String lastName;
}
