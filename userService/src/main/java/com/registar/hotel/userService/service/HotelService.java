package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.HotelDTO;

import java.util.List;
import java.util.Optional;

public interface HotelService {
    HotelDTO saveHotel(HotelDTO hotelDTO);
    HotelDTO saveHotel(CreateHotelRequest hotelRequest);
    Optional<HotelDTO> getHotelById(Long id);
    List<HotelDTO> getAllHotelsByOwnerId(Long userId);
    List<HotelDTO> getAllHotels();
    void deleteHotel(Long id);
    Optional<HotelDTO> updateHotel(Long hotelId, CreateHotelRequest hotelRequest);
}
