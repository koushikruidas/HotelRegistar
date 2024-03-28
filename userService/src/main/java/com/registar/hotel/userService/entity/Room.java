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
@ToString
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
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

    @ElementCollection
    @CollectionTable(name = "room_availability", joinColumns = @JoinColumn(name = "room_id"))
    @MapKeyTemporal(TemporalType.DATE)
    @Column(name = "isBooked")
    private Map<LocalDate, Boolean> bookingMap = new HashMap<>();

    public void setAvailabilityForDateRange(LocalDate startDate, LocalDate endDate, boolean isBooked) {
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            bookingMap.put(currentDate, isBooked);
            currentDate = currentDate.plusDays(1); // Increment by one day
        }
    }

    public boolean isAvailableForDateRange(LocalDate startDate, LocalDate endDate) {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (bookingMap.containsKey(date) && bookingMap.get(date)) {
                // Room is not available for this date
                return false;
            }
        }
        return true; // Room is available for the entire date range
    }
}



