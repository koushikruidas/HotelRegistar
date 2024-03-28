package com.registar.hotel.userService.repository;

import com.registar.hotel.userService.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {
    Optional<List<Guest>> findByMobileNo(String mobileNo);
    Optional<List<Guest>> findByName(String name);
    // Method to retrieve a single guest by name and mobile number (composite unique key)
    @Query("SELECT g FROM Guest g WHERE g.name = :name AND g.mobileNo = :mobileNo")
    Optional<Guest> findByNameAndMobileNo(@Param("name") String name, @Param("mobileNo") String mobileNo);
    @Query(value = "SELECT * FROM guest g WHERE g.name IN :names AND g.mobile_no IN :mobileNos", nativeQuery = true)
    List<Guest> findByNamesAndMobileNos(@Param("names") List<String> names, @Param("mobileNos") List<String> mobileNos);

}

