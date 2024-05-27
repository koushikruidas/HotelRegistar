package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.AdditionalServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalServicesRepository extends JpaRepository<AdditionalServices, Long> {
    List<AdditionalServices> findByBookings_Id(Long bookingId);
}
