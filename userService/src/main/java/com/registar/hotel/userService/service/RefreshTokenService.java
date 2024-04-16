package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.RefreshToken;
import com.registar.hotel.userService.model.RefreshTokenResponse;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username);
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshTokenResponse> findByUserId(Long id);
    RefreshToken verifyExpiration(RefreshToken token);
}
