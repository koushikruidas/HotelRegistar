package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.model.BookingWithGuestsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDTO saveBookingWithGuests(BookingWithGuestsDTO bookingWithGuestsDTO, MultipartFile[] govtIds, MultipartFile[] pictures);
    Optional<BookingDTO> getBookingById(int id);
    List<BookingDTO> getAllBookings();
    void deleteBooking(int id);
}
