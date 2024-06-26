package com.registar.hotel.userService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.registar.hotel.userService.entity.Address;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.request.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {
    private Long id;
    private String name;
    private String gstNumber;
    private AddressDTO address;
    private List<String> phoneNumbers;
    private UserDTO owner;
    private List<RoomDTO> rooms;
    private List<UserDTO> employees;
}
