package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.CreateUserRequest;
import com.registar.hotel.userService.model.UpdateUserRequest;
import com.registar.hotel.userService.model.UserDTO;
import com.registar.hotel.userService.repository.UserRepository;
import com.registar.hotel.userService.utility.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
            existingUser.get().setUsername(userDTO.getUsername());
            existingUser.get().setPassword(userDTO.getPassword());
            existingUser.get().setEmail(userDTO.getEmail());
            existingUser.get().setRole(userDTO.getRole());
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

