package com.blogen.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Entity representing an Avatar.
 * Each avatar has a unique filename and a generated ID.
 * 
 * Author: Cliff
 * Refine: Rachel
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique filename of the avatar.
     * This field is not updatable and must be unique.
     */
    @Column(unique = true, updatable = false)
    @NotNull(message = "File name cannot be null")
    @NotEmpty(message = "File name cannot be empty")
    private String fileName;

    // Custom methods if needed
}
