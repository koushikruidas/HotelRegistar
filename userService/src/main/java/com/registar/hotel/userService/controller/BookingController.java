package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.model.BookingWithGuestsDTO;
import com.registar.hotel.userService.service.BookingService;
import com.registar.hotel.userService.service.GuestService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private GuestService guestService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/create/booking/withGuest")
    public ResponseEntity<BookingDTO> createBookingWithGuests(@Valid @RequestBody BookingWithGuestsDTO bookingWithGuestsDTO) {
        BookingDTO createdBooking = bookingService.saveBookingWithGuests(bookingWithGuestsDTO);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable("id") int id) {
        Optional<BookingDTO> bookingOptional = bookingService.getBookingById(id);
        return bookingOptional.map(bookingDTO -> new ResponseEntity<>(bookingDTO, HttpStatus.OK))
                               .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<BookingDTO> allBookings = bookingService.getAllBookings();
        return new ResponseEntity<>(allBookings, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("id") int id) {
        bookingService.deleteBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
