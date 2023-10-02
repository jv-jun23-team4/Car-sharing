package com.example.car.sharing.controller;

import com.example.car.sharing.dto.user.UserDto;
import com.example.car.sharing.dto.user.UserRoleDto;
import com.example.car.sharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role", description = "Endpoint for updating a users roles")
    public void updateUserRoleById(@PathVariable Long id, @RequestBody UserRoleDto userRoleDto) {
        userService.updateUserRoleById(id, userRoleDto.getRole());
    }

    @GetMapping("/me")
    @Operation(summary = "Get user info", description = "Endpoint for getting a user info")
    public UserDto getMyProfileInfo(@RequestParam Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/me")
    @Operation(summary = "Update user", description = "Endpoint for updating a user")
    public UserDto update(@RequestBody UserDto updatedUserDto) {
        Long userId = updatedUserDto.getId();
        return userService.update(userId, updatedUserDto);
    }
}
