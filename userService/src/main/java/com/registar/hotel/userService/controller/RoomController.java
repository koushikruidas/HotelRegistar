package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/addRoom")
    public ResponseEntity<List<RoomDTO>> createRoom(@RequestBody List<RoomDTO> roomDTOs) {
        List<RoomDTO> createdRooms = roomDTOs.stream().map(i -> roomService.saveRoom(i))
                .collect(Collectors.toList());
        if (!createdRooms.isEmpty()) {
            return new ResponseEntity<>(createdRooms, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(createdRooms,HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getRoomById/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable("id") int id) {
        Optional<RoomDTO> roomOptional = roomService.getRoomById(id);
        return roomOptional.map(roomDTO -> new ResponseEntity<>(roomDTO, HttpStatus.OK))
                            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/getAllRooms")
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> allRooms = roomService.getAllRooms();
        return new ResponseEntity<>(allRooms, HttpStatus.OK);
    }

    @GetMapping("/getRoom/byAvailability")
    public ResponseEntity<List<RoomDTO>> getRoomsByAvailability(@RequestParam("availability") boolean availability) {
        List<RoomDTO> rooms = roomService.getRoomsByAvailability(availability);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") int id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
