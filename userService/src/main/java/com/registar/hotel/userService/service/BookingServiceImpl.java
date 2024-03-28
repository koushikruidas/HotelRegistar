package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Booking;
import com.registar.hotel.userService.entity.BookingStatus;
import com.registar.hotel.userService.entity.Guest;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.exception.RoomNotAvailableException;
import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.model.BookingWithGuestsDTO;
import com.registar.hotel.userService.model.GuestDTO;
import com.registar.hotel.userService.repository.BookingRepository;
import com.registar.hotel.userService.repository.GuestRepository;
import com.registar.hotel.userService.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookingDTO saveBooking(BookingDTO bookingDTO) {
        // Check if checkOutDate is after checkInDate
        if (bookingDTO.getCheckOutDate().isBefore(bookingDTO.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        Booking booking = modelMapper.map(bookingDTO, Booking.class);
        List<Room> rooms = bookingDTO.getBookedRoomIds().stream()
                .map(roomRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        booking.setBookedRooms(rooms);

        // Calculate the total price based on the prices of the booked rooms
        double totalPricePerNight = rooms.stream()
                .peek(i -> {
                    if (bookingDTO.getRoomPrice().containsKey(i.getId())){
                        i.setPricePerNight(bookingDTO.getRoomPrice().get(i.getId()));
                    }
                })
                .mapToDouble(Room::getPricePerNight)
                .sum();

        long daysBetween = 0;
        if (bookingDTO.getCheckInDate().equals(bookingDTO.getCheckOutDate())){
            daysBetween = 1;
        } else {
            daysBetween = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        }
        double totalPrice = totalPricePerNight * daysBetween;
        booking.setTotalPrice(totalPrice);
        Booking savedBooking = bookingRepository.save(booking);
        completeBooking(savedBooking.getId());
        return modelMapper.map(savedBooking, BookingDTO.class);
    }
    @Override
    @Transactional
    public BookingDTO saveBookingWithGuests(BookingWithGuestsDTO bookingWithGuestsDTO) {
        BookingDTO bookingDTO = bookingWithGuestsDTO.getBooking();
        List<GuestDTO> guestDTOs = bookingWithGuestsDTO.getGuests();


        LocalDate checkInDate = bookingDTO.getCheckInDate();
        LocalDate checkOutDate = bookingDTO.getCheckOutDate();

        List<Guest> guests = guestDTOs.stream()
                .map(guestDTO -> {
                    Optional<Guest> existingGuestOptional = guestRepository
                            .findByNameAndMobileNo(guestDTO.getName(),guestDTO.getMobileNo());
                    return existingGuestOptional
                            .map(existingGuest ->{
                                if (guestDTO.getGovtIDFilePath() != null) {
                                    existingGuest.setGovtIDFilePath(guestDTO.getGovtIDFilePath());
                                }
                                if (guestDTO.getPictureFilePath() != null) {
                                    existingGuest.setPictureFilePath(guestDTO.getPictureFilePath());
                                }
                                return existingGuest;
                            })
                            .orElseGet(() -> modelMapper.map(guestDTO, Guest.class));
                })
                .collect(Collectors.toList());

        guestRepository.saveAll(guests);

        Booking booking = modelMapper.map(bookingDTO, Booking.class);
        booking.setGuests(guests);

        List<Room> rooms = bookingDTO.getBookedRoomIds().stream()
                .map(roomRepository::findById)
                .flatMap(Optional::stream)
                .filter(room -> {
                    // Get the availability map for the room
                    Map<LocalDate, Boolean> availabilityMap = room.getBookingMap();

                    // Check availability for each date in the range
                    for (LocalDate date = checkInDate; !date.isAfter(checkOutDate); date = date.plusDays(1)) {
                        if (availabilityMap.getOrDefault(date, false)) {
                            // Room is not available for this date, so filter it out
                            throw new RoomNotAvailableException("Room: "+room.getRoomNumber()+" is not available for the date: "+date);
                        }
                    }
                    return true; // Room is available for the entire date range
                })
                .collect(Collectors.toList());
        booking.setBookedRooms(rooms);

        double totalPricePerNight = rooms.stream()
                .peek(room -> {
                    if (bookingDTO.getRoomPrice().containsKey(room.getId())) {
                        room.setPricePerNight(bookingDTO.getRoomPrice().get(room.getId()));
                    }
                })
                .mapToDouble(Room::getPricePerNight)
                .sum();

        long daysBetween = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        double totalPrice = totalPricePerNight * Math.max(daysBetween, 1); // Ensure at least 1 day is charged
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
