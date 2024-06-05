package com.blogen.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for a User of Blogen
 * 
 * Author: Cliff
 * Refine: Rachel
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "userName"})
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    @Column(nullable = false, unique = true)
    private String userName;

    @Transient
    private String password;

    private String encryptedPassword;

    private Boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USER_ROLE",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private UserPrefs userPrefs;

    /**
     * Adds a role to the user and ensures both sides of the relationship are properly maintained.
     * 
     * @param role the role to add
     */
    public void addRole(Role role) {
        if (!roles.contains(role)) {
            roles.add(role);
            role.getUsers().add(this);
        }
    }

    /**
     * Removes a role from the user and ensures both sides of the relationship are properly maintained.
     * 
     * @param role the role to remove
     */
    public void removeRole(Role role) {
        if (roles.remove(role)) {
            role.getUsers().remove(this);
        }
    }
}
