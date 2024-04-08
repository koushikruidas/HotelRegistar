package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.RoleName;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RoleController {

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<String> getRoles() {
        // Assume you have a predefined list of roles or you fetch them from a database
        List<RoleName> roles = Arrays.asList(RoleName.values());
        return roles.stream().map(RoleName::name).collect(Collectors.toList());
    }
}