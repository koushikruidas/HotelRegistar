package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.model.CreateRoomRequest;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.service.RoomService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/getRoomById/{id}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable("id") int id) {
        Optional<RoomDTO> roomOptional = roomService.getRoomById(id);
        return roomOptional.map(roomDTO -> new ResponseEntity<>(roomDTO, HttpStatus.OK))
                            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/getAllRooms/byHotelId")
    public ResponseEntity<List<RoomDTO>> getAllRooms(@RequestParam int id) {
        List<RoomDTO> allRooms = roomService.getAllRooms(id);
        return new ResponseEntity<>(allRooms, HttpStatus.OK);
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

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") int id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
