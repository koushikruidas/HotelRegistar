package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.Booking;
import com.registar.hotel.userService.model.response.BookingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT DISTINCT b FROM Booking b JOIN b.bookedRooms r WHERE b.checkInDate BETWEEN :start AND :end AND r.hotel.id = :hotelId")
    List<Booking> findBookingsByHotelIdAndDates(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("hotelId") Long hotelId);

    @Query("SELECT DISTINCT b FROM Booking b JOIN b.bookedRooms r WHERE r.hotel.id = :hotelId")
    List<Booking> getBookingByHotelId(long hotelId);

}
