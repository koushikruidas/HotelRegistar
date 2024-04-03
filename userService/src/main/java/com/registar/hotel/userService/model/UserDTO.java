package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Long Id;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String imageUrl;
    private Set<Role> roles;
    // Getters and setters
}

