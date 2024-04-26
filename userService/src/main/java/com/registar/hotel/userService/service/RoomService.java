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
    Optional<RoomDTO> getRoomById(Long id);
    List<RoomDTO> getAllRooms(Long id);
    List<RoomDTO> getAvailableRoomsForDateRange(LocalDate startDate, LocalDate endDate, List<Long> hotelIds);
    void deleteRoom(Long id);
}
