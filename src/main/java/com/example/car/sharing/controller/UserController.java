package com.example.car.sharing.controller;

import com.example.car.sharing.dto.user.UpdateUserData;
import com.example.car.sharing.dto.user.UpdateUserRole;
import com.example.car.sharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role", description = "Endpoint for updating a users roles")
    public void updateUserRoleById(@PathVariable Long id,
                                   @RequestBody UpdateUserRole updateUserRole) {
        userService.updateUserRoleById(id, updateUserRole.getRole());
    }

    @GetMapping("/me")
    @Operation(summary = "Get user info", description = "Endpoint for getting a user info")
    public UpdateUserData getMyProfileInfo() {
        return userService.getUserById();
    }

    @PutMapping("/me")
    @Operation(summary = "Update user's profile info")
    public UpdateUserData update(@RequestBody UpdateUserData updatedUpdateUserData) {
        return userService.update(updatedUpdateUserData);
    }
}
