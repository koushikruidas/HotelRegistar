package com.registar.hotel.userService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    private int roomNumber;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    private String customType;
    private double pricePerNight;
    private boolean availability;
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    @ManyToMany(mappedBy = "bookedRooms", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}



