package com.blogen.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain object representing the roles a user of Blogen may have.
 * Primarily used by Spring Security.
 * 
 * Author: Cliff
 * Refine: Rachel
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    /**
     * Adds a user to the role.
     * Ensures both sides of the relationship are properly maintained.
     * 
     * @param user the user to be added to the role
     */
    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            user.getRoles().add(this);
        }
    }

    /**
     * Removes a user from the role.
     * Ensures both sides of the relationship are properly maintained.
     * 
     * @param user the user to be removed from the role
     */
    public void removeUser(User user) {
        if (users.remove(user)) {
            user.getRoles().remove(this);
        }
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                '}';
    }
}
