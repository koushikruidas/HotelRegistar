package com.register.hotel.security.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_tokens", catalog = "social_login")
@Data
@NoArgsConstructor
public class BlockedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime blockedAt;

    public BlockedToken(String token) {
        this.token = token;
        this.blockedAt = LocalDateTime.now();
    }
}
