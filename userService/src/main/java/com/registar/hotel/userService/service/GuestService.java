package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.GuestDTO;

import java.util.List;
import java.util.Optional;

public interface GuestService {
    GuestDTO saveGuest(GuestDTO guestDTO);
    Optional<GuestDTO> getGuestById(int id);
    Optional<GuestDTO> getGuestByMobileNo(String mobileNo);
    Optional<List<GuestDTO>> getGuestByName(String name);
    List<GuestDTO> getAllGuests();
    void deleteGuest(int id);
}
