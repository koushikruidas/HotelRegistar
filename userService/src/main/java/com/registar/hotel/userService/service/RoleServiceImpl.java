package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.RoleName;
import com.registar.hotel.userService.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Set<Role> getDefaultRoles() {
        Set<Role> defaultRoles = new HashSet<>();
        defaultRoles.add(getRoleByName(RoleName.ROLE_EMPLOYEE));
        defaultRoles.add(getRoleByName(RoleName.ROLE_USER));
        return defaultRoles;
    }

    @Override
    public Role getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName);
    }

    @Override
    public Set<Role> getAllRoles() {
        return new HashSet<>(roleRepository.findAll());
    }
}
