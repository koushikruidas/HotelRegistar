package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.BookingDTO;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDTO saveBooking(BookingDTO bookingDTO);
    Optional<BookingDTO> getBookingById(int id);
    List<BookingDTO> getAllBookings();
    void deleteBooking(int id);
}
