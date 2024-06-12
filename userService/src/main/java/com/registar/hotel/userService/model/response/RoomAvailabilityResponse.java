package com.registar.hotel.userService.model.response;

import com.registar.hotel.userService.entity.RoomType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RoomAvailabilityResponse {
    private long id;
    private String roomNumber;
    private RoomType type;
    private String customType;
    private long hotelId;
    private double pricePerNight;
    private List<LocalDate> unavailableDays;
}
