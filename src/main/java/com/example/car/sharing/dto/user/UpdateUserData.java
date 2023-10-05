package com.example.car.sharing.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
public class UpdateUserData {
    @Length(min = 2, max = 50)
    private String firstName;
    @Length(min = 2, max = 50)
    private String lastName;
}
