package com.blogen.repositories;

import com.blogen.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for Category entities.
 * Provides methods to perform CRUD operations and custom queries on Category
 * data.
 * 
 * Author: Cliff
 * Refine: Rachel
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a Category by its exact name.
     * 
     * @param name the exact name of the Category
     * @return the Category with the specified name, or null if not found
     */
    Category findByName(String name);

    /**
     * Find Categories whose names contain the specified string, ignoring case.
     * This method automatically performs a case-insensitive search and wraps the
     * name variable with "%" for a LIKE search.
     * 
     * @param name the string to search for within Category names
     * @return a list of Categories whose names contain the specified string,
     *         ignoring case
     */
    List<Category> findByNameIgnoreCaseContaining(String name);

    /**
     * Retrieve a page of Categories based on pagination information.
     * 
     * @param pageable the pagination information
     * @return a page of Categories according to the pagination information
     */
    Page<Category> findAll(Pageable pageable);

}
