package com.registar.hotel.userService.utility;

import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.CreateUserRequest;
import com.registar.hotel.userService.model.UpdateUserRequest;
import com.registar.hotel.userService.model.UserDTO;

public interface UserMapper {
    UserDTO toDto(User entity);
    User toEntity(UserDTO dto);
    User toEntity(CreateUserRequest request);
    User toEntity(UpdateUserRequest request);
}

