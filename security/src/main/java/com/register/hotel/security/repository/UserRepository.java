package com.register.hotel.security.repository;


import com.register.hotel.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//	ArrayList<User> findAllUsers();
//	@Query("Select u from User u where u.firstName = :name")
//	public List<User> findByName(@Param("name") String name);
	
	Optional<User> findByEmail(String email);

    Optional<User> findByNameOrEmail(String name, String email);

    List<User> findByIdIn(List<Long> userIds);

    Optional<User> findByName(String name);
    
    Boolean existsByName(String username);

    Boolean existsByEmail(String email);
}