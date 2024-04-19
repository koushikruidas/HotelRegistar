package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.CreateRoomRequest;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.service.HotelService;
import com.registar.hotel.userService.service.RoomService;
import com.registar.hotel.userService.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<HotelDTO> createHotel(@RequestBody CreateHotelRequest hotelRequest) {
        HotelDTO createdHotel = hotelService.saveHotel(hotelRequest);
        return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable("id") Long id) {
        Optional<HotelDTO> hotelOptional = hotelService.getHotelById(id);
        return hotelOptional.map(hotelDTO -> new ResponseEntity<>(hotelDTO, HttpStatus.OK))
                            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/byOwner")
    public ResponseEntity<List<HotelDTO>> getAllHotelsByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        List<HotelDTO> hotels = hotelService.getAllHotelsByOwner(username);
        if (hotels != null) {
            return new ResponseEntity<>(hotels, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/employee/hotels")
    public ResponseEntity<List<HotelDTO>> getHotelsForEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> user = userService.getUserByEmail(username);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/")
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        List<HotelDTO> allHotels = hotelService.getAllHotels();
        return new ResponseEntity<>(allHotels, HttpStatus.OK);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> updateHotel(@PathVariable Long hotelId, @RequestBody CreateHotelRequest hotelRequest) {
        Optional<HotelDTO> updatedHotel = hotelService.updateHotel(hotelId, hotelRequest);
        return updatedHotel.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable("id") Long id) {
        hotelService.deleteHotel(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
