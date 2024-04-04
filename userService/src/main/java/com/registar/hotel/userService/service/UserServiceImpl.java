package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.RoleName;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.CreateUserRequest;
import com.registar.hotel.userService.model.UpdateUserRequest;
import com.registar.hotel.userService.model.UserDTO;
import com.registar.hotel.userService.repository.UserRepository;
import com.registar.hotel.userService.utility.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final RoleService roleService;
    private Logger logger = Logger.getLogger("UserServiceImpl");

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleService roleService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleService = roleService;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(userMapper::toDto);
    }

    @Override
    public UserDTO createUser(CreateUserRequest userDTO) {
        User user = userRepository.save(userMapper.toEntity(userDTO));
        return userMapper.toDto(user);
    }

    @Override
    public Optional<UserDTO> updateUser(Long userId, UpdateUserRequest userDTO) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            String newFirstName = userDTO.getFirstName();
            String newLastName = userDTO.getLastName();
            String newEmail = userDTO.getEmail();
            Set<Role> newRole = userDTO.getRoles();

            if (!newFirstName.isEmpty() && !newFirstName.isBlank()) {
                user.setFirstName(newFirstName);
            }
            if (!newLastName.isEmpty() && !newLastName.isBlank()) {
                user.setLastName(newLastName);
            }
            if (!newEmail.isEmpty() && !newEmail.isBlank()) {
                user.setEmail(newEmail);
            }

            // Check if the new role is not null
            if (newRole != null) {
                // Retrieve all available roles from the database
                Set<Role> allRoles = roleService.getAllRoles();

                // Check if all roles in newRole are valid (exist in the database)
                if (allRoles.containsAll(newRole)) {
                    user.setRoles(newRole);
                } else {
                    logger.warning("Invalid role(s) provided, keeping the previous role: " + user.getRoles());
                }
            } else {
                logger.warning("New role is null, keeping the previous role: " + user.getRoles());
            }
            // Update other fields as needed
            User updatedUser = userRepository.save(existingUser.get());
            return Optional.of(userMapper.toDto(updatedUser));
        }
        return Optional.empty();
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}

