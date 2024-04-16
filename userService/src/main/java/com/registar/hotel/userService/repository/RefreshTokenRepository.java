package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.id = :userId AND rt.id " +
            "= (SELECT MAX(rt2.id) FROM RefreshToken rt2 WHERE rt2.user.id = :userId)")
    Optional<RefreshToken> findLatestByUserId(Long userId);
}