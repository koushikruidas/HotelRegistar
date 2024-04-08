package com.registar.hotel.userService.bootstrap;

import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.RoleName;
import com.registar.hotel.userService.repository.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;


    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    @Transactional
    private void loadRoles() {
        RoleName[] roleNames = new RoleName[]{RoleName.ROLE_ADMIN, RoleName.ROLE_EMPLOYEE, RoleName.ROLE_USER, RoleName.ROLE_OWNER};

        List<Role> existingRoles = roleRepository.findAllByNameIn(Arrays.asList(roleNames));

        Set<RoleName> existingRoleNames = existingRoles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        List<Role> rolesToCreate = new ArrayList<>();
        for (RoleName roleName : roleNames) {
            if (!existingRoleNames.contains(roleName)) {
                Role roleToCreate = new Role();
                roleToCreate.setName(roleName);
                rolesToCreate.add(roleToCreate);
            }
        }
        roleRepository.saveAll(rolesToCreate);
    }
}