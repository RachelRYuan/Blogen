package com.blogen.api.v1.mappers;

import com.blogen.api.v1.model.CategoryDTO;
import com.blogen.domain.Category;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Utilizes MapStruct to map between Blogen {@link com.blogen.domain.Category} and
 * {@link com.blogen.api.v1.model.CategoryDTO}.
 * <p>
 * Provides methods to map entities to DTOs and vice versa, including a custom method for updating
 * an existing Category entity with data from a CategoryDTO.
 * </p>
 * <p>
 * This interface is implemented automatically by MapStruct at compile time.
 * </p>
 * <p>
 * Usage Example:
 * <pre>
 * {@code
 * CategoryMapper mapper = CategoryMapper.INSTANCE;
 * CategoryDTO dto = mapper.categoryToCategoryDto(category);
 * Category category = mapper.categoryDtoToCategory(dto);
 * mapper.updateCategoryFromCategoryDTO(dto, category);
 * }
 * </pre>
 * </p>
 * <p>
 * Note: Uses {@link com.blogen.api.v1.services.CategoryService#buildCategoryUrl(Category)} to set the category URL in the DTO.
 * </p>
 * </p>
 * </p>
 * </p>
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "categoryUrl", expression = "java(com.blogen.api.v1.services.CategoryService.buildCategoryUrl(category))")
    CategoryDTO categoryToCategoryDto(Category category);

    Category categoryDtoToCategory(CategoryDTO categoryDTO);

    void updateCategoryFromCategoryDTO(CategoryDTO requestDTO, @MappingTarget Category category);
}