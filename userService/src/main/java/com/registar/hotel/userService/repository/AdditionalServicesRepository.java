package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.AdditionalServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Repository
public interface AdditionalServicesRepository extends JpaRepository<AdditionalServices, Long> {
    List<AdditionalServices> findByBookings_Id(Long bookingId);
    @Query("SELECT s FROM AdditionalServices s JOIN s.bookings b WHERE b.checkInDate BETWEEN :start AND :end")
    List<AdditionalServices> findServicesByBookingDates(LocalDate start, LocalDate end);
}
