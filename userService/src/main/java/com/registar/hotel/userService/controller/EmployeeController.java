package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Hotel;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.model.HotelDTO;
import com.registar.hotel.userService.model.request.EmploymentRequest;
import com.registar.hotel.userService.service.HotelService;
import com.registar.hotel.userService.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private UserService userService;

    @Autowired
    private HotelService hotelService;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Update the hotels where the employee is employed.
     * @body EmploymentRequest with List of hotel IDs where the employee is to be employed.
     * @return ResponseEntity with status.
     */
}
