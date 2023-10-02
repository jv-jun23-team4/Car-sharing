package com.example.car.sharing.dto.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateUserData {
    @Length(min = 2, max = 50)
    private String firstName;
    @Length(min = 2, max = 50)
    private String lastName;
}
