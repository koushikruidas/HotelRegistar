package com.registar.hotel.userService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"bookings"})
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private int roomNumber;
    @Enumerated(EnumType.STRING)
    @Column(length = 255)
    private RoomType type;
    private String customType;
    private double pricePerNight;
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToMany(mappedBy = "bookedRooms", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public List<LocalDate> getUnavailableDaysForMonth(int year, int month) {
        List<LocalDate> unavailableDays = new ArrayList<>();

        // Iterate over each booking in the room
        for (Booking booking : bookings) {
            // Check if the booking overlaps with any day in the specified month
            for (LocalDate date = LocalDate.of(year, month, 1); date.getMonthValue() == month; date = date.plusDays(1)) {
                if (booking.getCheckInDate().isBefore(date.plusDays(1)) && booking.getCheckOutDate().isAfter(date.minusDays(1))) {
                    unavailableDays.add(date);
                }
            }
        }

        return unavailableDays;
    }
}



