package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Guest;
import com.registar.hotel.userService.model.GuestDTO;
import com.registar.hotel.userService.repository.GuestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public GuestDTO saveGuest(GuestDTO guestDTO) {
        Guest guest = modelMapper.map(guestDTO,Guest.class);
        Guest savedGuest = guestRepository.save(guest);
        return modelMapper.map(savedGuest,GuestDTO.class);
    }
    @Override
    public List<GuestDTO> saveAll(List<GuestDTO> guestDTOS){
        List<Guest> guests = guestDTOS.stream().map(i -> modelMapper.map(i, Guest.class)).collect(Collectors.toList());
        List<Guest> result = guestRepository.saveAll(guests);
        return result.stream().map(i ->modelMapper.map(i,GuestDTO.class)).toList();
    }

    @Override
    public Optional<GuestDTO> getGuestById(long id) {
        Optional<Guest> guestOptional = guestRepository.findById(id);
        return guestOptional.map(i -> modelMapper.map(i,GuestDTO.class));
    }

    @Override
    public Optional<List<GuestDTO>> getGuestByMobileNo(String mobileNo) {
        Optional<List<Guest>> guestOptional = guestRepository.findByMobileNo(mobileNo);
        return guestOptional.map(guests ->
                guests.stream()
                        .map(guest -> modelMapper.map(guest, GuestDTO.class))
                        .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<GuestDTO>> getGuestByName(String name) {
        Optional<List<Guest>> guestOptional = guestRepository.findByName(name);
        return Optional.of(guestOptional.stream().map(i -> modelMapper.map(i, GuestDTO.class)).collect(Collectors.toList()));
    }

    @Override
    public List<GuestDTO> getAllGuests() {
        List<Guest> guests = guestRepository.findAll();
        return guests.stream().map(i -> modelMapper.map(i,GuestDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteGuest(long id) {
        guestRepository.deleteById(id);
    }

    @Override
    public void update(GuestDTO guestDTO) {
        Guest guest = modelMapper.map(guestDTO,Guest.class);
        guestRepository.save(guest);
    }

    @Override
    public Optional<Guest> findByNameAndMobile(String name, String mobile) {
        return guestRepository.findByNameAndMobileNo(name,mobile);
    }
}
