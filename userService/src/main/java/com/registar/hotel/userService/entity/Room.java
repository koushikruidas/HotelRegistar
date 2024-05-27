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

    @Transient
    private boolean availableToday;
    public void updateAvailability() {
        this.availableToday = isAvailableForToday();
    }

    public boolean isAvailableForToday() {
        // Check if bookings are fetched and determine availability based on that
        for (Booking booking : bookings){
            if ( (booking.getCheckInDate().isBefore(LocalDate.now()) || booking.getCheckInDate().equals(LocalDate.now()) )
                    && booking.getCheckOutDate().isAfter(LocalDate.now())){
                return false;
            }
        }
        return true;
    }

    public List<LocalDate> getUnavailableDaysForMonth(int year, int month) {
        List<LocalDate> unavailableDays = new ArrayList<>();

        // Calculate the first and last days of the specified month
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        // Iterate over each booking in the room
        for (Booking booking : bookings) {
            LocalDate checkInDate = booking.getCheckInDate();
            // to exclude the checkout day, as if we check out means that day is available for another booking.
            LocalDate checkOutDate = booking.getCheckOutDate().minusDays(1);
            // Find the intersection between the booking period and the days of the month
            LocalDate intersectionStart = checkInDate.isAfter(firstDayOfMonth) ? checkInDate : firstDayOfMonth;
            LocalDate intersectionEnd = checkOutDate.isBefore(lastDayOfMonth) ? checkOutDate : lastDayOfMonth;

            // Add the intersection days to the unavailableDays list
            LocalDate currentDate = intersectionStart;
            while (!currentDate.isAfter(intersectionEnd)) {
                if (!unavailableDays.contains(currentDate)) {
                    unavailableDays.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
        }

        return unavailableDays;
    }
}



