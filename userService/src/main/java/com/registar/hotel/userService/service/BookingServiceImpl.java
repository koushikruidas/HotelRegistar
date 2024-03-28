package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Booking;
import com.registar.hotel.userService.entity.BookingStatus;
import com.registar.hotel.userService.entity.Guest;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.exception.RoomNotAvailableException;
import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.model.BookingWithGuestsDTO;
import com.registar.hotel.userService.model.GuestDTO;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.repository.BookingRepository;
import com.registar.hotel.userService.repository.GuestRepository;
import com.registar.hotel.userService.repository.RoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
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

   /* @Override
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
*/

    @Override
    @Transactional
    public BookingDTO saveBookingWithGuests(BookingWithGuestsDTO bookingWithGuestsDTO) {
        BookingDTO bookingDTO = bookingWithGuestsDTO.getBooking();
        List<GuestDTO> guestDTOs = bookingWithGuestsDTO.getGuests();

        LocalDate checkInDate = bookingDTO.getCheckInDate();
        LocalDate checkOutDate = bookingDTO.getCheckOutDate();

        // Fetch existing guests from the database
        Map<String, Guest> existingGuestsMap = fetchExistingGuests(guestDTOs);

        // Map guest DTOs to entities and set file paths if available
        List<Guest> guests = mapGuestDTOsToEntities(guestDTOs, existingGuestsMap);

        // Save all guests in a batch operation
        guestRepository.saveAll(guests);

        // Check room availability in a batch operation
        List<Room> availableRooms = findAvailableRooms(bookingWithGuestsDTO.getBooking());
        if (availableRooms.isEmpty()) throw new RoomNotAvailableException("Rooms are not available for the date range.");

        // Calculate total price
        double totalPrice = calculateTotalPrice(availableRooms, bookingDTO.getRoomPrice(), checkInDate, checkOutDate);

        // Create and save booking
        Booking booking = createBooking(bookingDTO, guests, availableRooms, totalPrice);
        Booking savedBooking = bookingRepository.save(booking);
        updateRoomAvailabilityForBooking(savedBooking);
        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    @Transactional(readOnly = true)
    private List<Room> findAvailableRooms(BookingDTO bookingDTO) {
        LocalDate checkInDate = bookingDTO.getCheckInDate();
        LocalDate checkOutDate = bookingDTO.getCheckOutDate();
        List<Integer> roomIds = bookingDTO.getBookedRooms().stream().map(RoomDTO::getId).collect(Collectors.toList());

        // Fetch availability for all rooms within the date range
        List<Room> availableRooms = roomRepository.findAvailableRoomsForDateRangeByRoomIds(checkInDate, checkOutDate, roomIds);

        // Filter out rooms that are not available for the entire date range
        return availableRooms.stream()
                .filter(room -> room.isAvailableForDateRange(checkInDate, checkOutDate))
                .collect(Collectors.toList());
    }

    @Transactional
    private Map<String, Guest> fetchExistingGuests(List<GuestDTO> guestDTOs) {
        List<String> names = guestDTOs.stream()
                .map(GuestDTO::getName)
                .toList();

        List<String> mobileNos = guestDTOs.stream()
                .map(GuestDTO::getMobileNo)
                .toList();

        List<Guest> existingGuests = guestRepository.findByNamesAndMobileNos(names, mobileNos);

        return existingGuests.stream()
                .collect(Collectors.toMap(guest -> guest.getName() + "_" + guest.getMobileNo(), Function.identity()));
    }

    private List<Guest> mapGuestDTOsToEntities(List<GuestDTO> guestDTOs, Map<String, Guest> existingGuestsMap) {
        return guestDTOs.stream()
                .map(guestDTO -> {
                    String key = guestDTO.getName() + "_" + guestDTO.getMobileNo();
                    Guest existingGuest = existingGuestsMap.get(key);
                    if (existingGuest != null) {
                        // Update existing guest if available
                        updateExistingGuest(existingGuest, guestDTO);
                        return existingGuest;
                    } else {
                        // Create new guest entity
                        return modelMapper.map(guestDTO, Guest.class);
                    }
                })
                .toList();
    }

    private void updateExistingGuest(Guest existingGuest, GuestDTO guestDTO) {
        if (guestDTO.getGovtIDFilePath() != null) {
            existingGuest.setGovtIDFilePath(guestDTO.getGovtIDFilePath());
        }
        if (guestDTO.getPictureFilePath() != null) {
            existingGuest.setPictureFilePath(guestDTO.getPictureFilePath());
        }
        // Update other fields if needed
    }

    private Booking createBooking(BookingDTO bookingDTO, List<Guest> guests, List<Room> rooms, double totalPrice) {
        Booking booking = modelMapper.map(bookingDTO, Booking.class);
        booking.setGuests(guests);
        booking.setBookedRooms(rooms);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.COMPLETED);
        return booking;
    }

    private double calculateTotalPrice(List<Room> rooms, Map<Integer, Double> roomPrices, LocalDate checkInDate, LocalDate checkOutDate) {
        long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return rooms.stream()
                .mapToDouble(room -> {
                    double price = roomPrices.getOrDefault(room.getId(), room.getPricePerNight());
                    return price * Math.max(daysBetween, 1); // Ensure at least 1 day is charged
                })
                .sum();
    }

    @Transactional
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

}
