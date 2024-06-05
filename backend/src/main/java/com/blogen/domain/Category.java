package com.blogen.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Domain Model for the Category of a Post
 * Author: Cliff
 * Refine: Rachel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique name of the category.
     * This field must not be null or empty.
     */
    @Column(unique = true)
    @NotNull(message = "Category name cannot be null")
    @NotEmpty(message = "Category name cannot be empty")
    private String name;

    /**
     * The date and time when the category was created.
     * Initialized to the current date and time.
     */
    private LocalDateTime created = LocalDateTime.now();
}
