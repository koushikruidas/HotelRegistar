package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByHotelIdAndAvailability(int id, boolean availability);
    List<Room> findByHotelId(int id);
}
