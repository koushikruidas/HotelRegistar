package com.registar.hotel.userService.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.registar.hotel.userService.entity.RoomPrice;
import com.registar.hotel.userService.model.RoomDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BookingResponse {
    private int Id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<RoomDTO> bookedRooms;

    @JsonIgnoreProperties({"booking","room"}) // Add this annotation to exclude the booking property
    private List<RoomPrice> roomPrices;
}
