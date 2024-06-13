package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.*;
import com.registar.hotel.userService.exception.ResourceNotFoundException;
import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.model.UserDTO;
import com.registar.hotel.userService.model.request.AddressDTO;
import com.registar.hotel.userService.model.response.HotelResponse;
import com.registar.hotel.userService.model.response.RoomAvailabilityResponse;
import com.registar.hotel.userService.repository.AddressRepository;
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
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private static final Logger logger = LoggerFactory.getLogger(HotelServiceImpl.class);

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository, UserRepository userRepository, ModelMapper modelMapper, AddressRepository addressRepository) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
    }

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

        Address address = modelMapper.map(hotelRequest.getAddress(), Address.class);
        // Save the Address entity first
        Address savedAddress = addressRepository.save(address);

        List<PhoneNumber> phoneNumbers = hotelRequest.getPhoneNumbers().stream().map(number -> {
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setNumber(number);
            phoneNumber.setHotel(hotel);
            return phoneNumber;
        }).toList();
        hotel.setPhoneNumbers(phoneNumbers);
        hotel.setEmployees(new HashSet<>(employees));
        hotel.setOwner(owner.get());
        hotel.setAddress(savedAddress);
        address.setHotel(hotel);
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
        user.ifPresent(hotelRepository::findByOwner);
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
        // Retrieve the existing hotel
        Optional<Hotel> existingHotelOptional = hotelRepository.findById(hotelId);
        if (existingHotelOptional.isEmpty()) {
            return Optional.empty();
        }

        Hotel existingHotel = existingHotelOptional.get();

        // Update hotel name
        if (!hotelRequest.getName().isEmpty() && !hotelRequest.getName().isBlank()) {
            existingHotel.setName(hotelRequest.getName());
        }

        // Update address
        AddressDTO addressDTO = hotelRequest.getAddress();
        if (addressDTO != null) {
            Address address = existingHotel.getAddress();
            if (address == null) {
                address = new Address();
                existingHotel.setAddress(address);
            }
            address.setStreet(addressDTO.getStreet());
            address.setCity(addressDTO.getCity());
            address.setState(addressDTO.getState());
            address.setZipCode(addressDTO.getZipCode());
            address.setCountry(addressDTO.getCountry());
        }

        if (hotelRequest.getPhoneNumbers() != null) {
            // Update phone numbers
            List<PhoneNumber> newPhoneNumbers = hotelRequest.getPhoneNumbers().stream()
                    .map(number ->
                            PhoneNumber.builder()
                                    .number(number)
                                    .hotel(existingHotel)
                                    .build()
                    )
                    .toList();

            existingHotel.getPhoneNumbers().clear();
            existingHotel.getPhoneNumbers().addAll(newPhoneNumbers);
        }

        if (!hotelRequest.getGstNumber().isEmpty() && !hotelRequest.getGstNumber().isBlank()) {
            // Update GST number
            existingHotel.setGstNumber(hotelRequest.getGstNumber());
        }

        // Update employees
        if (hotelRequest.getEmployeeIds() != null) {
            Set<User> employees = hotelRequest.getEmployeeIds().stream()
                    .map(userRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            existingHotel.setEmployees(employees);
        }

        // Save updated hotel
        Hotel updatedHotel = hotelRepository.save(existingHotel);

        // Convert updated hotel to HotelDTO
        HotelDTO updatedHotelDTO = modelMapper.map(updatedHotel, HotelDTO.class);

        return Optional.of(updatedHotelDTO);
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
