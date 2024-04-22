package com.registar.hotel.userService.model.response;

import com.registar.hotel.userService.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String firstName;
    private String lastName;
    private String username;
    private String accessToken;
    private String tokenType;
    private String token;
    private Set<Role> roles;
}
