package com.registar.hotel.userService.model;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private int roomNumber;
    private RoomType type;
    private double pricePerNight;
    private boolean availability;
}
