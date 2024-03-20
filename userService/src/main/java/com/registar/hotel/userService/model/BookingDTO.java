package com.registar.hotel.userService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {
    private int id;
    private int guestId;
    private String checkInDate;
    private String checkOutDate;
    private List<Integer> bookedRoomIds;
    private double totalPrice;
}
