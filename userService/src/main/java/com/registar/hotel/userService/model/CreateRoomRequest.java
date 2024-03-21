package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.RoomType;
import lombok.Data;

@Data
public class CreateRoomRequest {
    private int roomNumber;
    private RoomType type;
    private String customType;
    private double pricePerNight;
    private boolean availability;
}
