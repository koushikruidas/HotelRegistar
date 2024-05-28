package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.PhoneNumber;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.exception.ResourceNotFoundException;
import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.response.HotelResponse;
import com.registar.hotel.userService.model.response.RoomAvailabilityResponse;
import com.registar.hotel.userService.repository.HotelRepository;
import com.registar.hotel.userService.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(HotelServiceImpl.class);
    @Override
    @Transactional
    public HotelDTO saveHotel(CreateHotelRequest hotelRequest) {
        // Implement conversion from DTO to entity and vice versa

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> owner = userRepository.findByEmail(username);

        if (owner.isEmpty()) {
            throw new RuntimeException("Owner not present for email id: " + username);
        }

        List<User> employees = userRepository.findByIds(hotelRequest.getEmployeeIds());
        Hotel hotel = modelMapper.map(hotelRequest, Hotel.class);
        List<PhoneNumber> phoneNumbers = hotelRequest.getPhoneNumbers().stream().map(number -> {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setNumber(number);
            phoneNumber.setHotel(hotel);
            return phoneNumber;
        }).toList();
        hotel.setPhoneNumbers(phoneNumbers);
        hotel.setEmployees(new HashSet<>(employees));
        hotel.setOwner(owner.get());
        Hotel updated = hotelRepository.save(hotel);
        HotelDTO hotelDTO = modelMapper.map(updated, HotelDTO.class);
        hotelDTO.setPhoneNumbers(updated.getPhoneNumbers().stream()
                        .map(PhoneNumber::getNumber)
                        .collect(Collectors.toList()));
        return hotelDTO;
    }

    @Transactional
    public HotelDTO saveHotel(HotelDTO hotelDTO){
        Hotel hotel = hotelRepository.save(modelMapper.map(hotelDTO,Hotel.class));
        return modelMapper.map(hotel,HotelDTO.class);
    }

    @Override
    @Transactional
    public Optional<HotelDTO> getHotelById(Long id) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);
        return hotelOptional.map(i -> modelMapper.map(i, HotelDTO.class));
    }

    @Override
    @Transactional
    public List<HotelResponse> getAllHotelsByOwner(String username) {
        Optional<User> user = userRepository.findByEmail(username);
        user.ifPresent(i -> {
            hotelRepository.findByOwner(i);
        });
        if (user.isPresent()){
            List<Hotel> hotels = hotelRepository.findByOwner(user.get());
            return hotels.stream()
                    .map(this::mapHotelToHotelResponse)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private HotelResponse mapHotelToHotelResponse(Hotel hotel) {
        // Use ModelMapper for basic field mapping
        HotelResponse hotelResponse = modelMapper.map(hotel, HotelResponse.class);

        // Manually map the phone numbers
        List<String> phoneNumbers = hotel.getPhoneNumbers().stream()
                        .map(PhoneNumber::getNumber)
                        .collect(Collectors.toList());

        hotelResponse.setPhoneNumbers(phoneNumbers);

        return hotelResponse;
    }

    @Override
    @Transactional
    public List<HotelDTO> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream().map(i -> modelMapper.map(i, HotelDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<HotelDTO> updateHotel(Long hotelId, CreateHotelRequest hotelRequest) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);
        List<User> employees = userRepository.findByIds(hotelRequest.getEmployeeIds());

        if (optionalHotel.isPresent()) {
            Hotel hotel = optionalHotel.get();
            String newName = hotelRequest.getName();
            String newAddress = hotelRequest.getAddress();
            Set<User> empSet = new HashSet<>(employees);

            if (!newName.isEmpty() && !newName.isBlank()){
                hotel.setName(newName);
            }
            if (!newAddress.isEmpty() && !newAddress.isBlank()){
                hotel.setAddress(newAddress);
            }
            if (!employees.isEmpty()){
                hotel.setEmployees(empSet);
            }

            Hotel updatedHotel = hotelRepository.save(hotel);
            HotelDTO dto = modelMapper.map(updatedHotel, HotelDTO.class);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void save(Hotel hotel){
        hotelRepository.save(hotel);
    }

    @Override
    @Transactional
    public Optional<Hotel> findById(Long id){
        return hotelRepository.findById(id);
    }

    @Override
    @Transactional
    public List<HotelResponse> getHotelsForEmployee(String username) {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + username);
        }

        User user = userOptional.get();
        List<Hotel> employedHotels = hotelRepository.findHotelsByEmployeeId(user.getId());

        List<HotelResponse> hotelResponses = new ArrayList<>();
        for (Hotel hotel : employedHotels) {
            hotelResponses.add(modelMapper.map(hotel,HotelResponse.class));
        }

        return hotelResponses;
    }

    @Transactional
    public List<RoomAvailabilityResponse> getAvailabilityMapForMonth(Long hotelId, int year, int month) {
        List<RoomAvailabilityResponse> availabilityResponses = new ArrayList<>();
        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);
        if (optionalHotel.isEmpty()) {
            // Handle hotel not found
            return null;
        }

        Hotel hotel = optionalHotel.get();
        for (Room room : hotel.getRooms()) {
            List<LocalDate> unavailableDays = room.getUnavailableDaysForMonth(year, month);
             RoomAvailabilityResponse response = modelMapper.map(room, RoomAvailabilityResponse.class);
            response.setHotelId(room.getHotel().getId());
            response.setUnavailableDays(unavailableDays);
            availabilityResponses.add(response);
        }

        return availabilityResponses;
    }
}
