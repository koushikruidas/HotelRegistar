package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.model.CreateHotelRequest;
import com.registar.hotel.userService.model.CreateRoomRequest;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.service.HotelService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    private HotelService hotelService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/add")
    public ResponseEntity<HotelDTO> createHotel(@RequestBody CreateHotelRequest hotelRequest) {
        HotelDTO createdHotel = hotelService.saveHotel(hotelRequest);
        return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @PostMapping("/addRooms")
    public ResponseEntity<HotelDTO> addRooms(@RequestParam int hotelId, @RequestBody List<CreateRoomRequest> rooms){
        Optional<HotelDTO> hotelOptional = hotelService.getHotelById(hotelId);
        if (hotelOptional.isPresent()) {
            HotelDTO hotelDTO = hotelOptional.get();

            // changing the list to RoomDTO types
            List<RoomDTO> roomsDto = rooms.stream().map(i -> {
                RoomDTO room  = modelMapper.map(i,RoomDTO.class);
                room.setHotelId(hotelDTO.getId());
                return room;
            }).toList();

            hotelDTO.setRooms(roomsDto);
            hotelService.saveHotel(hotelDTO);
            return ResponseEntity.ok(hotelDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable("id") int id) {
        Optional<HotelDTO> hotelOptional = hotelService.getHotelById(id);
        return hotelOptional.map(hotelDTO -> new ResponseEntity<>(hotelDTO, HttpStatus.OK))
                            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/get/byOwner")
    public ResponseEntity<List<HotelDTO>> getAllHotelsByUserId(@RequestParam("ownerId") int ownerId) {
        List<HotelDTO> hotels = hotelService.getAllHotelsByOwnerId(ownerId);
        if (hotels != null) {
            return new ResponseEntity<>(hotels, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        List<HotelDTO> allHotels = hotelService.getAllHotels();
        return new ResponseEntity<>(allHotels, HttpStatus.OK);
    }

    @PutMapping("update/{hotelId}")
    public ResponseEntity<HotelDTO> updateHotel(@PathVariable int hotelId, @RequestBody CreateHotelRequest hotelRequest) {
        Optional<HotelDTO> updatedHotel = hotelService.updateHotel(hotelId, hotelRequest);
        return updatedHotel.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable("id") int id) {
        hotelService.deleteHotel(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
