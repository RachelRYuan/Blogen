package com.blogen.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Domain object that holds user-specific preferences for the Blogen web-site.
 * 
 * Author: Cliff
 * Refine: Rachel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class UserPrefs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    // The user's Avatar
    @OneToOne(fetch = FetchType.EAGER)
    private Avatar avatar;

    @Override
    public String toString() {
        return "UserPrefs{" +
                "id=" + id +
                ", userName=" + (user != null ? user.getUserName() : "null") +
                ", avatar='" + (avatar != null ? avatar.getFileName() : "null") + '\'' +
                '}';
    }
}
