package com.register.hotel.security.service;

import com.register.hotel.security.entity.BlockedToken;
import com.register.hotel.security.repository.BlockedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockedTokenServiceImpl implements BlockedTokenService {

    @Autowired
    private BlockedTokenRepository blockedTokenRepository;

    @Override
    public void blockToken(String token) {
        BlockedToken blockedToken = new BlockedToken(token);
        blockedTokenRepository.save(blockedToken);
    }

    @Override
    public boolean isTokenBlocked(String token) {
        boolean exists = blockedTokenRepository.existsByToken(token);
        return exists;
    }
}