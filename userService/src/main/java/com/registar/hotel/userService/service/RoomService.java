package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.model.CreateRoomRequest;
import com.registar.hotel.userService.model.RoomDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    void save(Room room);
    Room createRoom(CreateRoomRequest roomRequest);
    Optional<RoomDTO> getRoomById(int id);
    List<RoomDTO> getAllRooms(int id);
    List<RoomDTO> getAvailableRoomsForDateRange(LocalDate startDate, LocalDate endDate, List<Integer> hotelIds);
    void deleteRoom(int id);
}
