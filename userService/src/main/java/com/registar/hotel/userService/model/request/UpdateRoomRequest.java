package com.registar.hotel.userService.model.request;

import com.registar.hotel.userService.entity.RoomType;
import lombok.Data;

@Data
public class UpdateRoomRequest {
    private int Id;
    private int roomNumber;
    private RoomType type;
    private String customType;
    private double pricePerNight;
}
