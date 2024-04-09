package com.registar.hotel.userService.service;

import com.registar.hotel.userService.entity.RefreshToken;
import com.registar.hotel.userService.exception.ResourceNotFoundException;
import com.registar.hotel.userService.repository.RefreshTokenRepository;
import com.registar.hotel.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository.findByEmail(username)
                        .orElseThrow(() -> new ResourceNotFoundException("username: "+username+ " not available")))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) //10 minutes
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

}