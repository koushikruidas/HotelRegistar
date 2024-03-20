package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.Room;
import com.registar.hotel.userService.model.RoomDTO;
import com.registar.hotel.userService.repository.HotelRepository;
import com.registar.hotel.userService.repository.RoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<RoomDTO> saveRooms(List<RoomDTO> roomDTOs) {
        List<Room> rooms = roomDTOs.stream()
                .map(roomDTO -> {
                    Optional<Hotel> hotelOptional = hotelRepository.findById(roomDTO.getHotelId());
                    if (hotelOptional.isPresent()) {
                        Hotel hotel = hotelOptional.get();
                        roomDTO.setHotelId(hotel.getId());
                        return modelMapper.map(roomDTO, Room.class);
                    } else {
                        // Handle the case where the hotel for the room is not found
                        throw new RuntimeException("Hotel not found for room with ID: " + roomDTO.getId());
                    }
                })
                .collect(Collectors.toList());
        List<Room> savedRooms = roomRepository.saveAll(rooms);
        return savedRooms.stream().map(room -> modelMapper.map(room,RoomDTO.class)).collect(Collectors.toList());
    }

    @Override
    public Optional<RoomDTO> getRoomById(int id) {
        Optional<Room> roomOptional = roomRepository.findById(id);
        return roomOptional.map(room -> modelMapper.map(room, RoomDTO.class));
    }

    @Override
    public List<RoomDTO> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                    .map(room -> modelMapper.map(room, RoomDTO.class))
                    .collect(Collectors.toList());
    }

    @Override
    public List<RoomDTO> getRoomsByAvailability(boolean availability) {
        List<Room> rooms = roomRepository.findByAvailability(availability);
        return rooms.stream()
                .map(room -> modelMapper.map(room, RoomDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRoom(int id) {
        roomRepository.deleteById(id);
    }
}
