package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.model.BookingWithGuestsDTO;
import com.registar.hotel.userService.model.response.BookingList;
import com.registar.hotel.userService.model.response.BookingResponse;
import com.registar.hotel.userService.service.BookingService;
import com.registar.hotel.userService.service.GuestService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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

    @PostMapping
    public ResponseEntity<BookingResponse> createBookingWithGuests(@Valid @RequestPart BookingWithGuestsDTO bookingWithGuestsDTO,
                                                              @RequestParam("govtId") MultipartFile[] govtIds,
                                                              @RequestParam("picture") MultipartFile[] pictures) {
        BookingResponse createdBooking = bookingService.saveBookingWithGuests(bookingWithGuestsDTO, govtIds, pictures);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable("id") long id) {
        Optional<BookingResponse> bookingOptional = bookingService.getBookingById(id);
        return bookingOptional.map(bookingResponse -> new ResponseEntity<>(bookingResponse, HttpStatus.OK))
                               .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> allBookings = bookingService.getAllBookings();
        return new ResponseEntity<>(allBookings, HttpStatus.OK);
    }
    @GetMapping("/hotel/{hotelId}/startDate/{startDate}/endDate/{endDate}")
    public ResponseEntity<List<BookingList>> getAllBookingsByHotel(@PathVariable(name = "hotelId") long hotelId,
                                                                   @PathVariable(name = "startDate") LocalDate startDate,
                                                                   @PathVariable(name = "endDate") LocalDate endDate){
        List<BookingList> bookingResponses = bookingService.getBookingByHotelIdAndDate(hotelId, startDate, endDate);
        if (bookingResponses == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(bookingResponses, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable("id") long id) {
        bookingService.deleteBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
