package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.model.CreateRoomRequest;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.model.request.UpdateRoomRequest;
import com.registar.hotel.userService.service.HotelService;
import com.registar.hotel.userService.service.RoomService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private ModelMapper modelMapper;
    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable("id") int id) {
        Optional<RoomDTO> roomOptional = roomService.getRoomById(id);
        return roomOptional.map(roomDTO -> new ResponseEntity<>(roomDTO, HttpStatus.OK))
                            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<List<RoomDTO>> getAllRooms(@PathVariable("hotelId") Long hotelId) {
        List<RoomDTO> allRooms = roomService.getAllRooms(hotelId);
        return new ResponseEntity<>(allRooms, HttpStatus.OK);
    }

    @PostMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> addRooms(@PathVariable("hotelId") Long hotelId,
                                             @RequestBody List<CreateRoomRequest> rooms){
        Optional<HotelDTO> hotelOptional = hotelService.getHotelById(hotelId);
        if (hotelOptional.isPresent()) {
            HotelDTO hotelDTO = hotelOptional.get();

            // changing the list to RoomDTO types
            List<RoomDTO> roomsDto = rooms.stream()
                    .map(room -> {
                        Room createdRoom = roomService.createRoom(room);
                        createdRoom.setHotel(modelMapper.map(hotelDTO, Hotel.class));
                        return modelMapper.map(createdRoom, RoomDTO.class);
                    }).toList();

            hotelDTO.setRooms(roomsDto);
            hotelService.saveHotel(hotelDTO);
            return ResponseEntity.ok(hotelDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDTO> updateRooms(@PathVariable("hotelId") Long hotelId,
                                                @RequestBody List<UpdateRoomRequest> rooms) {
        Optional<HotelDTO> hotelOptional = hotelService.getHotelById(hotelId);
        if (hotelOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HotelDTO hotelDTO = hotelOptional.get();
        List<RoomDTO> updatedRooms = new ArrayList<>();

        for (UpdateRoomRequest roomRequest : rooms) {
            Optional<RoomDTO> roomOptional = roomService.getRoomById(roomRequest.getId());
            if (roomOptional.isPresent()) {
                Room room = modelMapper.map(roomOptional.get(), Room.class);
                modelMapper.map(roomRequest, room); // Update the room entity with new data
                room.setHotel(modelMapper.map(hotelDTO, Hotel.class)); // Re-associate the room with the hotel
                roomService.save(room);
                updatedRooms.add(modelMapper.map(room, RoomDTO.class));
            } else {
                // Optionally, handle the case where a room ID does not exist.
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        hotelDTO.setRooms(updatedRooms);
        hotelService.saveHotel(hotelDTO);
        return ResponseEntity.ok(hotelDTO);
    }

    @GetMapping("/available")
    public ResponseEntity<List<RoomDTO>> getAvailableRoomsForDateRange(
            @Parameter(in = ParameterIn.QUERY, description = "Start date in the format dd-MM-yyyy", required = true, schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy"))
            @RequestParam(name = "startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,

            @Parameter(in = ParameterIn.QUERY, description = "End date in the format dd-MM-yyyy", required = true, schema = @Schema(type = "string", format = "date", pattern = "dd-MM-yyyy"))
            @RequestParam(name = "endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,

            @Parameter(in = ParameterIn.QUERY, description = "List of hotel IDs", required = true)
            @RequestParam(name = "hotelIds") List<Integer> hotelIds) {

        List<RoomDTO> availableRooms = roomService.getAvailableRoomsForDateRange(startDate, endDate, hotelIds);
        return ResponseEntity.ok(availableRooms);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") int id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
