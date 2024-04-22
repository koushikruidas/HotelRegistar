package com.registar.hotel.userService.entity;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"hotels", "employedHotels"})
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    private String imageUrl;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Hotel> hotels; // If owner

    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    private Set<Hotel> employedHotels; // Hotels where the user is employed


    private boolean isAccountExpired = false;
    private boolean isAccountLocked = false;
    private boolean isCredentialsExpired = false;
    private boolean isEnabled = true;

    @Override
    public int hashCode() {
        return Objects.hash(Id, email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(Id, other.Id) &&
                Objects.equals(email, other.email);
    }
}