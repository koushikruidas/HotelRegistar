package com.registar.hotel.userService.model;

import com.registar.hotel.userService.annotation.CheckInAfterOrEqualToCurrentDate;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private int id;
    @NotEmpty(message = "Guest ID list cannot be empty")
    private List<Integer> guestId;
    @NotNull(message = "Check-in date cannot be null")
    @CheckInAfterOrEqualToCurrentDate
    private LocalDate checkInDate;
    @NotNull(message = "cannot be empty")
    private LocalDate checkOutDate;
    @NotEmpty(message = "Booked room IDs list cannot be empty")
    private List<Integer> bookedRoomIds;
    private Map<Integer,Double> roomPrice;
}
