package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User user);
    @Query("SELECT h FROM Hotel h JOIN h.employees e WHERE e.id = :userId")
    List<Hotel> findHotelsByEmployeeId(@Param("userId") Long userId);
}
