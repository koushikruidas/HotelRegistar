package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.exception.ResourceNotFoundException;
import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.CreateRoomRequest;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.UserDTO;
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
        HotelDTO hotelDTO = modelMapper.map(hotelRequest, HotelDTO.class);
        if (owner.isPresent()) {
            UserDTO ownerDto = modelMapper.map(owner.get(),UserDTO.class);
            hotelDTO.setOwner(ownerDto);
            return modelMapper.map(hotelRepository.save(modelMapper.map(hotelDTO,Hotel.class)), HotelDTO.class);
        }
        else {
            throw new RuntimeException("owner not present for mail id: "+username);
        }
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
    public List<HotelDTO> getAllHotelsByOwner(String username) {
        Optional<User> user = userRepository.findByEmail(username);
        user.ifPresent(i -> {
            hotelRepository.findByOwner(i);
        });
        if (user.isPresent()){
            List<Hotel> hotels = hotelRepository.findByOwner(user.get());
            return hotels.stream()
                    .map(hotel -> modelMapper.map(hotel, HotelDTO.class))
                    .collect(Collectors.toList());
        }
        return null;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        if (optionalHotel.isPresent()) {
            Hotel hotel = optionalHotel.get();
            String newName = hotelRequest.getName();
            String newAddress = hotelRequest.getAddress();

            if (!newName.isEmpty() && !newName.isBlank()){
                hotel.setName(newName);
            }
            if (!newAddress.isEmpty() && !newAddress.isBlank()){
                hotel.setAddress(newAddress);
            }

            // Find owner by username from request
            if (!username.isEmpty() && !username.isBlank()) {
                Optional<User> owner = userRepository.findByEmail(username);
                if (owner.isPresent()) {
                    hotel.setOwner(owner.get());
                } else {
                    // Handle case where owner is not found
                    // You may throw an exception or return an error response
                    logger.error("owner is not present: "+ username);
                    throw new RuntimeException("owner not present for username: "+ username);
                }
            }
            // Update other fields as needed

            Hotel updatedHotel = hotelRepository.save(hotel);
            return Optional.of(modelMapper.map(updatedHotel, HotelDTO.class));
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
    public List<HotelDTO> getHotelsForEmployee(String username) {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + username);
        }

        User user = userOptional.get();
        List<Hotel> employedHotels = hotelRepository.findHotelsByEmployeeId(user.getId());

        List<HotelDTO> hotelDTOs = new ArrayList<>();
        for (Hotel hotel : employedHotels) {
            hotelDTOs.add(modelMapper.map(hotel,HotelDTO.class));
        }

        return hotelDTOs;
    }
}
