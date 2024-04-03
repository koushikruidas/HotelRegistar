package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private Set<Role> roles;
    // Getters and setters
}
