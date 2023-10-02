package com.example.car.sharing.service.impl;

import com.example.car.sharing.dto.user.UserRegistrationRequestDto;
import com.example.car.sharing.dto.user.UserRegistrationResponseDto;
import com.example.car.sharing.exception.RegistrationException;
import com.example.car.sharing.mapper.UserMapper;
import com.example.car.sharing.model.User;
import com.example.car.sharing.repository.UserRepository;
import com.example.car.sharing.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserRegistrationResponseDto register(
            UserRegistrationRequestDto userRequestDto) throws RegistrationException {
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with such an email " + userRequestDto.getEmail()
                    + "already exists in the system.");
        }
        User user = userMapper.toModel(userRequestDto);
        user.setRole(User.UserRole.CUSTOMER);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(User.UserRole.CUSTOMER);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException("Can't find a user with email "
                        + authentication.getName())
        );
    }
}
