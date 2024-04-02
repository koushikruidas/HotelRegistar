package com.register.hotel.security.repository;

import com.register.hotel.security.entity.BlockedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedTokenRepository extends JpaRepository<BlockedToken, Long> {
	boolean existsByToken(String token);
}