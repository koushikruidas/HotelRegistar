package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long id);
    @Query(value = "SELECT * FROM room r " +
            "WHERE r.hotel_id IN (:hotelIds) " +
            "AND NOT EXISTS (SELECT 1 FROM room_availability ra " +
            "                WHERE ra.room_id = r.id " +
            "                AND ra.booking_map_key BETWEEN :startDate AND :endDate " +
            "                AND ra.is_booked = true)", nativeQuery = true)
    List<Room> findAvailableRoomsForDateRange(LocalDate startDate, LocalDate endDate, List<Long> hotelIds);

    @Query(value = "SELECT r.* FROM room r " +
            "WHERE r.id IN (:roomIds) " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM booking b " +
            "    JOIN booking_room br ON b.id = br.booking_id " +
            "    WHERE br.room_id = r.id " +
            "    AND b.check_in_date <= :endDate " +
            "    AND b.check_out_date >= :startDate" +
            ")", nativeQuery = true)
    List<Room> findAvailableRoomsForDateRangeByRoomIds(LocalDate startDate, LocalDate endDate, List<Long> roomIds);
}
