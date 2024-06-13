package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.model.CreateRoomRequest;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.model.response.AvailabilityRoomDTO;
import com.registar.hotel.userService.repository.HotelRepository;
import com.registar.hotel.userService.repository.RoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<RoomDTO> getRoomById(Long id) {
        Optional<Room> roomOptional = roomRepository.findById(id);
        return roomOptional.map(room -> modelMapper.map(room, RoomDTO.class));
    }

    @Override
    public List<AvailabilityRoomDTO> getAllRooms(Long id) {
        List<Room> rooms = roomRepository.findByHotelId(id);
        rooms.forEach(Room::updateAvailability);
        return rooms.stream()
                    .map(room -> modelMapper.map(room, AvailabilityRoomDTO.class))
                    .collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> getAvailableRoomsForDateRange(LocalDate startDate, LocalDate endDate, List<Long> hotelIds) {
        List<Room> availableRooms = roomRepository.findAvailableRoomsForDateRange(startDate, endDate, hotelIds);
        return availableRooms.stream().map(room -> modelMapper.map(room,RoomDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public void save(Room room) {
        roomRepository.save(room);
    }

    @Override
    public Room createRoom(CreateRoomRequest roomRequest) {
        Room room = new Room();
        room.setRoomNumber(roomRequest.getRoomNumber());
        room.setType(roomRequest.getType());
        room.setCustomType(roomRequest.getCustomType());
        room.setPricePerNight(roomRequest.getPricePerNight());

        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void saveAllRooms(List<Room> rooms) {
        roomRepository.saveAll(rooms);
    }
}
