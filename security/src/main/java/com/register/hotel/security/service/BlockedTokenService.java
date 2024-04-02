package com.register.hotel.security.service;

public interface BlockedTokenService {
    void blockToken(String token);
    boolean isTokenBlocked(String token);
}
