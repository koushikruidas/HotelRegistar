package com.registar.hotel.userService.model;

import com.registar.hotel.userService.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private int id;
    private int roomNumber;
    private RoomType type;
    private String customType;
    private double pricePerNight;
    private Map<LocalDate, Boolean> bookingMap;
    private int hotelId;
}
