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
    private List<GuestList> guests;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
