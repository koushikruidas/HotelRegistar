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


    /**
     *
     * @PostLoad, @PostPersist, @PostUpdate:
     * These annotations ensure that the updateAvailability method is called automatically after an entity is loaded
     * from the database or persisted/updated. This method sets the value of availableToday by calling isAvailableForToday().
     *
     * isAvailableForToday Method: This method checks if the room is available today by iterating
     * over the bookings list and determining if any booking overlaps with today.
     * */
    @PostLoad
    @PostPersist
    @PostUpdate
    public void updateAvailability() {
        this.availableToday = isAvailableForToday();
    }

    public boolean isAvailableForToday() {
        LocalDate today = LocalDate.now();
        if (bookings == null) {
            return true;
        }
        for (Booking booking : bookings) {
            if (booking.getCheckInDate().isBefore(today.plusDays(1)) && booking.getCheckOutDate().isAfter(today.minusDays(1))) {
                return false;
            }
        }
        return true;
    }

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



