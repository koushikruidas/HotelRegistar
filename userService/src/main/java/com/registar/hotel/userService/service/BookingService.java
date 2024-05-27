package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.model.BookingWithGuestsDTO;
import com.registar.hotel.userService.model.response.BookingResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingResponse saveBookingWithGuests(BookingWithGuestsDTO bookingWithGuestsDTO, MultipartFile[] govtIds, MultipartFile[] pictures);
    Optional<BookingResponse> getBookingById(long id);
    List<BookingResponse> getAllBookings();
    void deleteBooking(long id);
}
