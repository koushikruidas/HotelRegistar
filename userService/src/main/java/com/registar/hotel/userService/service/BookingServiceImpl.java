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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<Room> rooms = bookingDTO.getBookedRoomIds().stream()
                .map(roomRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        booking.setBookedRooms(rooms);

        // Calculate the total price based on the prices of the booked rooms
        double totalPricePerNight = rooms.stream()
                .mapToDouble(Room::getPricePerNight)
                .sum();
        long daysBetween = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());

        double totalPrice = totalPricePerNight*daysBetween;
        booking.setTotalPrice(totalPrice);
        Booking savedBooking = bookingRepository.save(booking);
        completeBooking(savedBooking.getId());
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

        for (Room room : bookedRooms){
            // Update room availability for the booked date range
            room.setAvailabilityForDateRange(checkInDate, checkOutDate, true);
        }
        // Save all the updated rooms in a single batch operation
        roomRepository.saveAll(bookedRooms);
    }
}
