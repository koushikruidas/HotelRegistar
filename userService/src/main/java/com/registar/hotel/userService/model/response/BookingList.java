package com.registar.hotel.userService.model.response;

import com.registar.hotel.userService.model.GuestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingList {
    private long id;
    private int roomNo;
    private List<GuestList> guests;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double bookingPrice;
    private List<AdditionalServicesDTO> additionalServices;
    private double totalPrice;
}
