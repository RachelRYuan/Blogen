package com.blogen.api.v1.services;

import com.blogen.api.v1.controllers.CategoryController;
import com.blogen.api.v1.model.CategoryDTO;
import com.blogen.api.v1.model.CategoryListDTO;
import com.blogen.domain.Category;

/**
 * Service for managing Blogen categories in the REST API.
 */
public interface CategoryService {

    /**
     * Retrieves a 'page' worth of categories.
     *
     * @param pageNum  The page number of categories to retrieve using 0-based indices.
     * @param pageSize The maximum number of categories to retrieve per page.
     * @return A CategoryListDTO containing the categories for the specified page.
     */
    CategoryListDTO getCategories(int pageNum, int pageSize);

    /**
     * Get a specific Category.
     *
     * @param id The Category ID to search for.
     * @return A CategoryDTO representing the Blogen {@link com.blogen.domain.Category}.
     */
    CategoryDTO getCategory(Long id);

    /**
     * Create a new Blogen Category.
     *
     * @param categoryDTO Contains the details of the Category to create.
     * @return A CategoryDTO representing the newly created Category.
     */
    CategoryDTO createNewCategory(CategoryDTO categoryDTO);

    /**
     * Update a specific Category.
     *
     * @param id          The ID of the Category to update.
     * @param categoryDTO DTO containing the fields to update.
     * @return A CategoryDTO containing the details of the updated Category.
     */
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    /**
     * Build a URL for the specified Category.
     *
     * @param category The Category for which to build the URL.
     * @return A string representing the URL of the Category.
     */
    static String buildCategoryUrl(Category category) {
        return CategoryController.BASE_URL + "/" + category.getId();
    }
}
