package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.HotelDTO;

import java.util.List;
import java.util.Optional;

public interface HotelService {
    HotelDTO saveHotel(HotelDTO hotelDTO);
    HotelDTO saveHotel(CreateHotelRequest hotelRequest);
    Optional<HotelDTO> getHotelById(int id);
    List<HotelDTO> getAllHotelsByOwnerId(int userId);
    List<HotelDTO> getAllHotels();
    void deleteHotel(int id);
}
