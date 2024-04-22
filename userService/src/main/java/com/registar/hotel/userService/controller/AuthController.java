package com.registar.hotel.userService.controller;

import com.registar.hotel.userService.entity.RefreshToken;
import com.registar.hotel.userService.entity.Role;
import com.registar.hotel.userService.entity.User;
import com.registar.hotel.userService.exception.BadRequestException;
import com.registar.hotel.userService.exception.ResourceNotFoundException;
import com.registar.hotel.userService.model.RefreshTokenResponse;
import com.registar.hotel.userService.model.request.LoginRequest;
import com.registar.hotel.userService.model.request.RefreshTokenRequest;
import com.registar.hotel.userService.model.request.SignUpRequest;
import com.registar.hotel.userService.model.response.ApiResponse;
import com.registar.hotel.userService.model.response.AuthenticationResponse;
import com.registar.hotel.userService.repository.UserRepository;
import com.registar.hotel.userService.securityUtil.TokenProvider;
import com.registar.hotel.userService.service.BlockedTokenService;
import com.registar.hotel.userService.service.RefreshTokenService;
import com.registar.hotel.userService.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${app.jwtSecret}")
    private String secret;

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

    @Autowired
    private RoleService roleService;

    @Autowired
    private RefreshTokenService refreshTokenService;

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
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
        if (user.isEmpty()) throw new ResourceNotFoundException("User not found in the database");
        AuthenticationResponse response = AuthenticationResponse.builder()
                .firstName(user.get().getFirstName())
                .lastName(user.get().getLastName())
                .username(loginRequest.getEmail())
                .accessToken(token)
                .token(refreshToken.getToken())
                .tokenType("Bearer")
                .roles(user.get().getRoles())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
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

        Set<Role> roles = roleService.getDefaultRoles();
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

    @PostMapping("/refreshToken")
    public AuthenticationResponse refreshToken(@RequestParam Long userId, @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                            String accessToken = tokenProvider.generateToken(userId);
                            return AuthenticationResponse.builder()
                                    .accessToken(accessToken)
                                    .token(refreshTokenRequest.getToken())
                                    .tokenType("Bearer")
                                    .build();
                        }
                ).orElseThrow(() -> new RuntimeException(
                        "Refresh token is not in database!"));
    }
    @GetMapping("/getRefreshToken")
    public ResponseEntity<RefreshTokenResponse> getRefreshToken(@RequestParam Long userId){
        Optional<RefreshTokenResponse> token = refreshTokenService.findByUserId(userId);
        return new ResponseEntity<>(token.orElseThrow(() -> new ResourceNotFoundException("Token not found")), HttpStatus.OK);
    }

}