package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.RoleName;
import com.registar.hotel.userService.repository.RoleRepository;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Role> getRoles() {
        // Assume you have a predefined list of roles or you fetch them from a database
        return roleRepository.findAll();
    }
}