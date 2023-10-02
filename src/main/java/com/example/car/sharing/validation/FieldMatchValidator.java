package com.example.car.sharing.validation;

import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch,
        UserRegistrationRequestDto> {
    private String field;
    private String fieldMatch;

    @Override
    public boolean isValid(UserRegistrationRequestDto request,
                           ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(request.getPassword(), request.getRepeatPassword());
    }
}
