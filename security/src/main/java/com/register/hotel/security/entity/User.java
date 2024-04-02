package com.register.hotel.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.register.hotel.security.utility.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
}, catalog = "social_login")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false)
    private String email;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "oauth_user_roles", catalog = "social_login",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Column
    private Set<Role> roles = new HashSet<>();
}