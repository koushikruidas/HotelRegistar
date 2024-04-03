package com.registar.hotel.userService.service;

public interface BlockedTokenService {
    void blockToken(String token);
    boolean isTokenBlocked(String token);
}
