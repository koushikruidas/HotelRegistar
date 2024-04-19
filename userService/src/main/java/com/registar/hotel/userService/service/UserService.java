package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.CreateUserRequest;
import com.registar.hotel.userService.model.UpdateUserRequest;
import com.registar.hotel.userService.model.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    Optional<User> getUserByEmail(String email);
    Optional<UserDTO> getUserById(Long userId);
    UserDTO createUser(CreateUserRequest userRequest);
    Optional<UserDTO> updateUser(Long userId, UpdateUserRequest updateUserRequest);
    void deleteUser(Long userId);
    void save(User user);
}

