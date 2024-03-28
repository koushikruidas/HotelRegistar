package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Guest;
import com.registar.hotel.userService.model.GuestDTO;

import java.util.List;
import java.util.Optional;

public interface GuestService {
    GuestDTO saveGuest(GuestDTO guestDTO);
    List<GuestDTO> saveAll(List<GuestDTO> guestDTOS);
    Optional<GuestDTO> getGuestById(int id);
    Optional<List<GuestDTO>> getGuestByMobileNo(String mobileNo);
    Optional<List<GuestDTO>> getGuestByName(String name);
    List<GuestDTO> getAllGuests();
    void deleteGuest(int id);
    void update(GuestDTO guestDTO);
    Optional<Guest> findByNameAndMobile(String name, String mobile);
}
