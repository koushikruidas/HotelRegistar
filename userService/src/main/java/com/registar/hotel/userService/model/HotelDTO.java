package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {
    private Long id;
    private String name;
    private String address;
    private UserDTO owner;
    private List<RoomDTO> rooms;
    private List<User> employees;
}
