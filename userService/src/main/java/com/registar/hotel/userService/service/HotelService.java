package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.response.HotelResponse;
import com.registar.hotel.userService.model.response.RoomAvailabilityResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HotelService {
    HotelDTO saveHotel(HotelDTO hotelDTO);
    HotelDTO saveHotel(CreateHotelRequest hotelRequest);
    Optional<HotelDTO> getHotelById(Long id);
    Optional<Hotel> findById(Long id);
    List<HotelResponse> getAllHotelsByOwner(String username);
    List<HotelDTO> getAllHotels();
    void deleteHotel(Long id);
    Optional<HotelDTO> updateHotel(Long hotelId, CreateHotelRequest hotelRequest);
    void save(Hotel hotel);
    List<HotelResponse> getHotelsForEmployee(String username);
    List<RoomAvailabilityResponse> getAvailabilityMapForMonth(Long hotelId, int year, int month);
}
