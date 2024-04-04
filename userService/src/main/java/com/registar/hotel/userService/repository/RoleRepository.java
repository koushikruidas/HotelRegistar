package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName roleName);
}
