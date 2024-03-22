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
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(unique = true)
    private String mobileNo;
    private String govIDFilePath; // File path for government IDs
    private String pictureFilePath; // File path for guest picture
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}


