package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private int id;
    private int roomNumber;
    private RoomType type;
    private String customType;
    private double pricePerNight;
    private boolean availability;
    private int hotelId;
}
