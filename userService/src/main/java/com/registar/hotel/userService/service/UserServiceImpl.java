package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.CreateUserRequest;
import com.registar.hotel.userService.model.UpdateUserRequest;
import com.registar.hotel.userService.model.UserDTO;
import com.registar.hotel.userService.repository.UserRepository;
import com.registar.hotel.userService.utility.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private Logger logger = Logger.getLogger("UserServiceImpl");

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(userMapper::toDto);
    }

    @Override
    public UserDTO createUser(CreateUserRequest userDTO) {
        User user = userRepository.save(userMapper.toEntity(userDTO));
        return userMapper.toDto(user);
    }

    @Override
    public Optional<UserDTO> updateUser(int userId, UpdateUserRequest userDTO) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            String newUsername = userDTO.getUsername();
            String newPassword = userDTO.getPassword();
            String newEmail = userDTO.getEmail();
            Role newRole = userDTO.getRole();

            if (!newUsername.isEmpty() && !newUsername.isBlank()) {
                user.setUsername(newUsername);
            }
            if (!newPassword.isEmpty() && !newPassword.isBlank()) {
                user.setPassword(newPassword);
            }
            if (!newEmail.isEmpty() && !newEmail.isBlank()) {
                user.setEmail(newEmail);
            }

            // Check if the new role is not null and is valid
            if (newRole != null && Arrays.asList(Role.values()).contains(newRole)) {
                user.setRole(newRole);
            } else {
                logger.warning("Role not found, hence keeping the previous role: "+user.getRole());
            }
            // Update other fields as needed
            User updatedUser = userRepository.save(existingUser.get());
            return Optional.of(userMapper.toDto(updatedUser));
        }
        return Optional.empty();
    }

    @Override
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }
}

