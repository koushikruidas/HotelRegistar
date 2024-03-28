package com.registar.hotel.userService.model;

import com.registar.hotel.userService.annotation.CheckInAfterOrEqualToCurrentDate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class BookingDTO {
    @NotNull(message = "Check-in date cannot be null")
    @CheckInAfterOrEqualToCurrentDate
    private LocalDate checkInDate;

    @NotNull(message = "cannot be empty")
    private LocalDate checkOutDate;

    @NotEmpty(message = "Booked rooms list cannot be empty")
    private List<RoomDTO> bookedRooms;

    private Map<Integer,Double> roomPrice;
}
