package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private Long Id;
    private String roomNumber;
    private RoomType type;
    private String customType;
    private double pricePerNight;
    private Long hotelId;
}
