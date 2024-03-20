package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {
    Optional<Guest> findByMobileNo(String mobileNo);
    Optional<List<Guest>> findByName(String name);
}

