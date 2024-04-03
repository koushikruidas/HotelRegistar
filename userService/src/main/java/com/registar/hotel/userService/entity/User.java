package com.registar.hotel.userService.entity;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String firstName;
    private String lastName;
    private String password;

    @Column(unique = true)
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    private String imageUrl;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Hotel> hotels; // If owner

    private boolean isAccountExpired = false;
    private boolean isAccountLocked = false;
    private boolean isCredentialsExpired = false;
    private boolean isEnabled = true;
}



