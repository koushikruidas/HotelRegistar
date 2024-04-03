package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.exception.BadRequestException;
import com.registar.hotel.userService.model.request.LoginRequest;
import com.registar.hotel.userService.model.request.SignUpRequest;
import com.registar.hotel.userService.model.response.ApiResponse;
import com.registar.hotel.userService.model.response.AuthenticationResponse;
import com.registar.hotel.userService.repository.UserRepository;
import com.registar.hotel.userService.securityUtil.TokenProvider;
import com.registar.hotel.userService.service.BlockedTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private BlockedTokenService blockedTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);
//        return ResponseEntity.ok(new AuthenticationResponse(token));
        return new ResponseEntity<>(new AuthenticationResponse(token), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());

        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.HOTEL_OWNER);
        roles.add(Role.EMPLOYEE);
        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Check if the string contains "Bearer "
        if (token.contains("Bearer ")) {
            // If it does, remove "Bearer " from the string
            token = token.replace("Bearer ", "");
        }

        // Add the token to the blocklist
        blockedTokenService.blockToken(token);
        return new ResponseEntity<>("Logged out successfully!!",HttpStatus.OK);
    }

}