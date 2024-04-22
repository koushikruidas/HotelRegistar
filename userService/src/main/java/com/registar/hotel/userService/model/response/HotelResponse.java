package com.registar.hotel.userService.model.response;

import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.model.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelResponse {
    private Long id;
    private String name;
    private String address;
    private UserDTO owner;
    private List<RoomDTO> rooms;
    private Set<UserDTO> employees;
}
