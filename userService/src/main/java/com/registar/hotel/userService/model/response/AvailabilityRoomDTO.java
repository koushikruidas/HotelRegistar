package com.registar.hotel.userService.model.response;

import com.registar.hotel.userService.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityRoomDTO {
    private Long Id;
    private int roomNumber;
    private RoomType type;
    private String customType;
    private double pricePerNight;
    private boolean availableToday;
    private Long hotelId;
}
