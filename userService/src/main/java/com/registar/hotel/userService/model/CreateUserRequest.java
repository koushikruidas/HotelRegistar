package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequest {
    @NotEmpty(message = "firstName cannot be empty.")
    private String firstName;

    @NotEmpty(message = "lastName cannot be empty.")
    private String lastName;

    @NotEmpty(message = "password cannot be empty.")
    @Size(min = 8, message = "password must be at least 8 character long.")
    private String password;

    @NotEmpty(message = "email cannot be empty.")
    private String email;

    private Set<Role> roles;
    // Getters and setters
}

