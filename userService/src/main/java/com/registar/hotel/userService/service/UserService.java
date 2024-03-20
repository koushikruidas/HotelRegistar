package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.CreateUserRequest;
import com.registar.hotel.userService.model.UpdateUserRequest;
import com.registar.hotel.userService.model.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(int userId);
    UserDTO createUser(CreateUserRequest userRequest);
    Optional<UserDTO> updateUser(int userId, UpdateUserRequest updateUserRequest);
    void deleteUser(int userId);
}

