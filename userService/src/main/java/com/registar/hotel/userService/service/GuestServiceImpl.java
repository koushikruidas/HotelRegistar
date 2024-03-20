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
        Guest guest = new Guest();
        guest.setName(guestDTO.getName());
        guest.setMobileNo(guestDTO.getMobileNo());
        guest.setGovIDFilePath(guestDTO.getGovIDFilePath());
        guest.setPictureFilePath(guestDTO.getPictureFilePath());
        Guest savedGuest = guestRepository.save(guest);
        return modelMapper.map(savedGuest,GuestDTO.class);
    }

    @Override
    public Optional<GuestDTO> getGuestById(int id) {
        Optional<Guest> guestOptional = guestRepository.findById(id);
        return guestOptional.map(i -> modelMapper.map(i,GuestDTO.class));
    }

    @Override
    public Optional<GuestDTO> getGuestByMobileNo(String mobileNo) {
        Optional<Guest> guestOptional = guestRepository.findByMobileNo(mobileNo);
        return guestOptional.map(i -> modelMapper.map(i,GuestDTO.class));
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
    public void deleteGuest(int id) {
        guestRepository.deleteById(id);
    }
}
