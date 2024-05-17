package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.CreateRoomRequest;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.model.response.HotelResponse;
import com.registar.hotel.userService.service.HotelService;
import com.registar.hotel.userService.service.RoomService;
import com.registar.hotel.userService.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping
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

    @GetMapping
    public ResponseEntity<List<HotelResponse>> getAllHotelsByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = ((UserDetails) (authentication.getPrincipal())).getAuthorities();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        List<HotelResponse> hotels = null;
        if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER"))) {
            hotels = hotelService.getAllHotelsByOwner(username);
        } else if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_EMPLOYEE"))){
            hotels = hotelService.getHotelsForEmployee(username);
        }
        if (hotels != null && !hotels.isEmpty()) {
            return new ResponseEntity<>(hotels, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{hotelId}")
    @PreAuthorize("hasRole('ROLE_OWNER')")
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

    // Endpoint to add an employee to a hotel
    @PostMapping("/{hotelId}/employees/{userId}")
    public ResponseEntity<?> addEmployeeToHotel(@PathVariable Long hotelId, @PathVariable Long userId) {
        // Check if hotel exists
        Optional<Hotel> hotelOptional = hotelService.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if user exists
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Hotel hotel = hotelOptional.get();
        User user = userOptional.get();

        // Add user as an employee to the hotel
        hotel.getEmployees().add(user);
        hotelService.save(hotel);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{hotelId}/employees/{userId}")
    public ResponseEntity<?> removeEmployeeFromHotel(@PathVariable Long hotelId, @PathVariable Long userId) {
        // Check if hotel exists
        Optional<Hotel> hotelOptional = hotelService.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if user exists
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Hotel hotel = hotelOptional.get();
        User user = userOptional.get();

        // Remove user from the list of employees of the hotel
        if (!hotel.getEmployees().remove(user)) {
            return ResponseEntity.notFound().build(); // User was not found in the list of employees
        }

        // Save the updated hotel
        hotelService.save(hotel);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{hotelId}/availability")
    public ResponseEntity<Map<Room, List<LocalDate>>> getAvailabilityForMonth(
            @PathVariable Long hotelId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        Map<Room, List<LocalDate>> availabilityMap = hotelService.getAvailabilityMapForMonth(hotelId, year, month);
        if (availabilityMap == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(availabilityMap, HttpStatus.OK);
    }


/*
    // Endpoint to get all hotels for an employee
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/employees/hotels")
    public ResponseEntity<List<HotelDTO>> getHotelsForEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ( (UserDetails) authentication.getPrincipal()).getUsername();
        List<HotelDTO> hotels = hotelService.getHotelsForEmployee(username);
        return ResponseEntity.ok(hotels);
    }*/

}
