package com.registar.hotel.userService.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "blocked_tokens")
@Data
@NoArgsConstructor
public class BlockedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String token;

    private LocalDateTime blockedAt;

    public BlockedToken(String token) {
        this.token = token;
        // Get the current time in UTC
        this.blockedAt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}
