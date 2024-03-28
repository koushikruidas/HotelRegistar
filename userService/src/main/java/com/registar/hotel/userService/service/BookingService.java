package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.model.BookingWithGuestsDTO;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDTO saveBookingWithGuests(BookingWithGuestsDTO bookingWithGuestsDTO);
    Optional<BookingDTO> getBookingById(int id);
    List<BookingDTO> getAllBookings();
    void deleteBooking(int id);
}
