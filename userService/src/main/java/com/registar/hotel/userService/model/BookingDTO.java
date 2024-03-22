package com.registar.hotel.userService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private int id;
    private List<Integer> guestId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<Integer> bookedRoomIds;
}
