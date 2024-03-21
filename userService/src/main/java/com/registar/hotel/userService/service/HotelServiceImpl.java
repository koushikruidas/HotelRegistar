package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.UserDTO;
import com.registar.hotel.userService.repository.HotelRepository;
import com.registar.hotel.userService.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    public HotelDTO saveHotel(CreateHotelRequest hotelRequest) {
        // Implement conversion from DTO to entity and vice versa
        Optional<User> owner = userRepository.findByUsername(hotelRequest.getOwnerUserName());
        HotelDTO hotelDTO = modelMapper.map(hotelRequest, HotelDTO.class);
        if (owner.isPresent()) {
            UserDTO ownerDto = modelMapper.map(owner.get(),UserDTO.class);
            hotelDTO.setOwner(ownerDto);
            return modelMapper.map(hotelRepository.save(modelMapper.map(hotelDTO,Hotel.class)), HotelDTO.class);
        }
        else {
            throw new RuntimeException("owner not present for username: "+hotelRequest.getOwnerUserName());
        }
    }

    public HotelDTO saveHotel(HotelDTO hotelDTO){
        Hotel hotel = hotelRepository.save(modelMapper.map(hotelDTO,Hotel.class));
        return modelMapper.map(hotel,HotelDTO.class);
    }

    @Override
    public Optional<HotelDTO> getHotelById(int id) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);
        return hotelOptional.map(i -> modelMapper.map(i, HotelDTO.class));
    }

    @Override
    public List<HotelDTO> getAllHotelsByOwnerId(int ownerId) {
        Optional<User> user = userRepository.findById(ownerId);
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
    public List<HotelDTO> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream().map(i -> modelMapper.map(i, HotelDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteHotel(int id) {
        hotelRepository.deleteById(id);
    }

    @Override
    public Optional<HotelDTO> updateHotel(int hotelId, CreateHotelRequest hotelRequest) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(hotelId);
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
            String ownerUsername = hotelRequest.getOwnerUserName();
            if (!ownerUsername.isEmpty() && !ownerUsername.isBlank()) {
                Optional<User> owner = userRepository.findByUsername(ownerUsername);
                if (owner.isPresent()) {
                    hotel.setOwner(owner.get());
                } else {
                    // Handle case where owner is not found
                    // You may throw an exception or return an error response
                    logger.error("owner is not present: "+ownerUsername);
                    throw new RuntimeException("owner not present for username: "+ownerUsername);
                }
            }
            // Update other fields as needed

            Hotel updatedHotel = hotelRepository.save(hotel);
            return Optional.of(modelMapper.map(updatedHotel, HotelDTO.class));
        }
        return Optional.empty();
    }

}
