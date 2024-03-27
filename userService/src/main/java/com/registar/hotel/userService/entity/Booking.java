package com.registar.hotel.userService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private BookingStatus status;

    @ManyToMany
    @JoinTable(
            name = "booking_room",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private List<Room> bookedRooms;

    /**
     * If all Guest entities associated with a Booking are removed from the guests list,
     * the Booking entity will be deleted from the database automatically due to orphan removal.
     */
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinTable(
            name = "booking_guest",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id")
    )
    private List<Guest> guests;
}



