package com.example.car.sharing.controller;

import com.example.car.sharing.dto.user.UserLoginRequestDto;
import com.example.car.sharing.dto.user.UserLoginResponseDto;
import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import com.example.car.sharing.dto.user.UserRegistrationResponseDto;
import com.example.car.sharing.exception.RegistrationException;
import com.example.car.sharing.security.AuthentificationService;
import com.example.car.sharing.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthentificationService authentificationService;

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authentificationService.authenticate(request);
    }

    @PostMapping(value = "/register")
    public UserRegistrationResponseDto register(@RequestBody @Valid
                                                    UserRegistrationRequestDto userRequestDto)
            throws RegistrationException {
        return userService.register(userRequestDto);
    }
}
