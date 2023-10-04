package com.example.car.sharing.controller;

import com.example.car.sharing.dto.user.UpdateUserData;
import com.example.car.sharing.dto.user.UpdateUserRole;
import com.example.car.sharing.dto.user.UserDto;
import com.example.car.sharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role by ID", description = "The manager can "
            + "change the role of the user from CUSTOMER to MANAGER, and vice versa, "
            + "thereby providing and removing access rights for this user.")
    public UserDto updateUserRoleById(@PathVariable Long id,
                                   @RequestBody UpdateUserRole updateUserRole) {
        return userService.updateUserRoleById(id, updateUserRole.getRole());
    }

    @GetMapping("/me")
    @Operation(summary = "Get user info", description = "The user can view "
            + "information from the user's profile")
    public UserDto getMyProfileInfo() {
        return userService.getUserInfo();
    }

    @PatchMapping("/me")
    @Operation(summary = "Update user's profile info",
            description = "The user can change information from the user's profile")
    public UserDto update(@RequestBody UpdateUserData updatedUpdateUserData) {
        return userService.update(updatedUpdateUserData);
    }
}
