package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.Role;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
    // Getters and setters
}

