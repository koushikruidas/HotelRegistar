package com.registar.hotel.userService.model;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class BookingWithGuestsDTO {
    @Valid
    private BookingDTO booking;

    @Valid
    private List<GuestDTO> guests;
}
