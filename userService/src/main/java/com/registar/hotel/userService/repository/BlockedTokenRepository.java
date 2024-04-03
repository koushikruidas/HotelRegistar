package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.BlockedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedTokenRepository extends JpaRepository<BlockedToken, Long> {
	boolean existsByToken(String token);
}