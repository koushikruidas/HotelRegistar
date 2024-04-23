package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.*;
import com.registar.hotel.userService.exception.ResourceNotFoundException;
import com.registar.hotel.userService.exception.RoomNotAvailableException;
import com.registar.hotel.userService.model.BookingDTO;
import com.registar.hotel.userService.model.BookingWithGuestsDTO;
import com.registar.hotel.userService.model.GuestDTO;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.model.response.BookingResponse;
import com.registar.hotel.userService.repository.BookingRepository;
import com.registar.hotel.userService.repository.GuestRepository;
import com.registar.hotel.userService.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private S3Service s3Service;
    @Autowired
    private ModelMapper modelMapper;
    @PersistenceContext
    private EntityManager entityManager;

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
    public BookingResponse saveBookingWithGuests(BookingWithGuestsDTO bookingWithGuestsDTO,
                                            MultipartFile[] govtIds, MultipartFile[] pictures) {
        BookingDTO bookingDTO = bookingWithGuestsDTO.getBooking();
        List<GuestDTO> guestDTOs = bookingWithGuestsDTO.getGuests();

        LocalDate checkInDate = bookingDTO.getCheckInDate();
        LocalDate checkOutDate = bookingDTO.getCheckOutDate();

        // Upload government IDs and pictures for each guest
        List<CompletableFuture<String>> govtIdUploadFutures = new ArrayList<>();
        List<CompletableFuture<String>> picUploadFutures = new ArrayList<>();

        for (int i = 0; i < guestDTOs.size(); i++) {
            GuestDTO guestDTO = guestDTOs.get(i);
            MultipartFile govtId = govtIds[i];
            MultipartFile picture = pictures[i];

            govtIdUploadFutures.add(uploadFileAsync(govtId, guestDTO.getName(), guestDTO.getMobileNo()));
            picUploadFutures.add(uploadFileAsync(picture, guestDTO.getName(), guestDTO.getMobileNo()));
        }

        // Wait for all file uploads to complete
        CompletableFuture.allOf(govtIdUploadFutures.toArray(new CompletableFuture[0])).join();
        CompletableFuture.allOf(picUploadFutures.toArray(new CompletableFuture[0])).join();

        // Set file paths for each guest
        for (int i = 0; i < guestDTOs.size(); i++) {
            GuestDTO guestDTO = guestDTOs.get(i);
            guestDTO.setGovtIDFilePath(govtIdUploadFutures.get(i).join());
            guestDTO.setPictureFilePath(picUploadFutures.get(i).join());
        }


        // Fetch existing guests from the database
        Map<String, Guest> existingGuestsMap = fetchExistingGuests(guestDTOs);

        // Map guest DTOs to entities and set file paths if available
        List<Guest> guests = mapGuestDTOsToEntities(guestDTOs, existingGuestsMap);

        // Save all guests in a batch operation
        List<Guest> guests1 = guestRepository.saveAll(guests);

        // Check room availability in a batch operation
        List<Room> availableRooms = findAvailableRooms(bookingWithGuestsDTO.getBooking());
        if (availableRooms.size() != bookingWithGuestsDTO.getBooking().getBookedRooms().size()) {
            throw new RoomNotAvailableException("All selected Rooms are not available for the date range. Only "
                    + availableRooms.size() + " rooms are available.");
        }

        // Calculate total price
        double totalPrice = calculateTotalPrice(availableRooms, bookingDTO.getRoomPrice(), checkInDate, checkOutDate);

        // Create and save booking
        Booking booking = createBooking(bookingDTO, guests1, availableRooms, totalPrice);

        // Create and save room price entities
        List<RoomPrice> roomPrices = new ArrayList<>();
        for (RoomDTO roomDTO : bookingDTO.getBookedRooms()) {
            Room room = roomRepository.findById(roomDTO.getId()).orElseThrow(() -> new RuntimeException("Room not found"));
            RoomPrice roomPrice = new RoomPrice();
            roomPrice.setRoom(room);
            if (bookingDTO.getRoomPrice().containsKey(roomDTO.getId())){
                roomPrice.setPrice(bookingDTO.getRoomPrice().get(roomDTO.getId()));
            } else {
                roomPrice.setPrice(roomDTO.getPricePerNight()); // Set the price from the DTO
            }
            roomPrice.setBooking(booking);
            roomPrices.add(roomPrice);
        }
        // Set room prices to the booking entity
        booking.setRoomPrices(roomPrices);

        Booking savedBooking = bookingRepository.save(booking);
        updateRoomAvailabilityForBooking(savedBooking);
        return modelMapper.map(savedBooking, BookingResponse.class);
    }

    // Helper method to upload file asynchronously
    private CompletableFuture<String> uploadFileAsync(MultipartFile file, String name, String mobileNo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return s3Service.uploadFileForGuest(file, name, mobileNo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
                        // Reattach the existing guest
                        return entityManager.merge(existingGuest);
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

        List<Room> managedRooms = new ArrayList<>();
        for (Room room : bookedRooms){
            // Update room availability for the booked date range
            room.setAvailabilityForDateRange(checkInDate, checkOutDate, true);
            managedRooms.add(entityManager.merge(room)); // Reattach the room
        }
        // Save all the updated rooms in a single batch operation
        roomRepository.saveAll(managedRooms);
    }


    @Override
    @Transactional
    public Optional<BookingResponse> getBookingById(int id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isEmpty()) throw new ResourceNotFoundException("Not Found!!");
        BookingResponse map = modelMapper.map(bookingOptional.get(), BookingResponse.class);
        return bookingOptional.map(booking -> modelMapper.map(booking, BookingResponse.class));
    }

    @Override
    @Transactional
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBooking(int id) {
        bookingRepository.deleteById(id);
    }

}
