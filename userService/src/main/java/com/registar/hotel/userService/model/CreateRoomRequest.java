package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.RoomType;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class CreateRoomRequest {
    private int roomNumber;
    private RoomType type;
    private String customType;
    private double pricePerNight;
}
