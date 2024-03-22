package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Booking;
import com.registar.hotel.userService.entity.BookingStatus;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.repository.BookingRepository;
import com.registar.hotel.userService.repository.RoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookingDTO saveBooking(BookingDTO bookingDTO) {
        Booking booking = modelMapper.map(bookingDTO, Booking.class);
        Booking savedBooking = bookingRepository.save(booking);
        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    @Override
    public Optional<BookingDTO> getBookingById(int id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        return bookingOptional.map(booking -> modelMapper.map(booking, BookingDTO.class));
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(int id) {
        bookingRepository.deleteById(id);
    }

    public void completeBooking(int bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        optionalBooking.ifPresent(booking -> {
            // Mark the booking as completed
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);

            // Update room availability for the booked date range
            updateRoomAvailabilityForBooking(booking);
        });
    }

    private void updateRoomAvailabilityForBooking(Booking booking) {
        List<Room> bookedRooms = booking.getBookedRooms();
        LocalDate checkInDate = booking.getCheckInDate();
        LocalDate checkOutDate = booking.getCheckOutDate();

        for (Room room : bookedRooms) {
            // Find the room entity from the database to ensure it's managed
            Room managedRoom = roomRepository.findById(room.getId()).orElse(null);
            if (managedRoom != null) {
                // Update room availability for the booked date range
                managedRoom.setAvailabilityForDateRange(checkInDate, checkOutDate, true);
                roomRepository.save(managedRoom);
            }
        }
    }
}
