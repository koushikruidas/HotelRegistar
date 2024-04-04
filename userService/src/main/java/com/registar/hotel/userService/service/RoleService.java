package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.RoleName;

import java.util.Set;

public interface RoleService {
    Set<Role> getDefaultRoles();
    Role getRoleByName(RoleName roleName);
    Set<Role> getAllRoles();
}
