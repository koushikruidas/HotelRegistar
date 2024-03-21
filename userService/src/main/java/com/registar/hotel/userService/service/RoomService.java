package com.registar.hotel.userService.service;

import com.registar.hotel.userService.model.RoomDTO;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<RoomDTO> saveRooms(List<RoomDTO> roomDTOs);
    Optional<RoomDTO> getRoomById(int id);
    List<RoomDTO> getAllRooms(int id);
    List<RoomDTO> getRoomsByAvailability(int id, boolean availability);
    void deleteRoom(int id);
}
