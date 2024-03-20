package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    List<Hotel> findByOwner(User user);
}
