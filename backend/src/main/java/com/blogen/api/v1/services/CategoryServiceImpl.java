package com.blogen.api.v1.services;

import com.blogen.api.v1.mappers.CategoryMapper;
import com.blogen.api.v1.model.CategoryDTO;
import com.blogen.api.v1.model.CategoryListDTO;
import com.blogen.domain.Category;
import com.blogen.exceptions.BadRequestException;
import com.blogen.repositories.CategoryRepository;
import com.blogen.services.utils.PageRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing Blogen categories in the REST API.
 * This service supports CRUD operations on categories, but only Blogen admins can perform these operations.
 *
 * Deleting categories is not supported in this API for now.
 * 
 * Author: Cliff
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PageRequestBuilder pageRequestBuilder;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
                               PageRequestBuilder pageRequestBuilder) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.pageRequestBuilder = pageRequestBuilder;
    }

    @Override
    public CategoryListDTO getCategories(int pageNum, int pageSize) {
        PageRequest pageRequest = pageRequestBuilder.buildPageRequest(pageNum, pageSize, Sort.Direction.DESC, "id");
        Page<Category> page = categoryRepository.findAllBy(pageRequest);
        List<CategoryDTO> categoryDTOs = new ArrayList<>();
        page.forEach(category -> {
            CategoryDTO dto = categoryMapper.categoryToCategoryDto(category);
            categoryDTOs.add(dto);
        });
        return new CategoryListDTO(categoryDTOs, PageRequestBuilder.buildPageInfoResponse(page));
    }

    @Override
    public CategoryDTO getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category with id: " + id + " does not exist"));
        CategoryDTO categoryDTO = categoryMapper.categoryToCategoryDto(category);
        categoryDTO.setCategoryUrl(CategoryService.buildCategoryUrl(category));
        return categoryDTO;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Override
    public CategoryDTO createNewCategory(CategoryDTO categoryDTO) {
        try {
            Category categoryToSave = categoryMapper.categoryDtoToCategory(categoryDTO);
            Category savedCategory = categoryRepository.save(categoryToSave);
            CategoryDTO savedDTO = categoryMapper.categoryToCategoryDto(savedCategory);
            savedDTO.setCategoryUrl(CategoryService.buildCategoryUrl(savedCategory));
            return savedDTO;
        } catch (DataIntegrityViolationException e) {
            String message = String.format("Category already exists with name %s", categoryDTO.getName());
            log.error(message, e);
            throw new DataIntegrityViolationException(message);
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category does not exist with id: " + id));
        categoryMapper.updateCategoryFromCategoryDTO(categoryDTO, category);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDto(savedCategory);
    }
}
