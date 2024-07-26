package com.blogen.api.v1.validators;

import com.blogen.api.v1.model.CategoryDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for CategoryDTOs
 */
@Component
public class CategoryDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CategoryDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CategoryDTO categoryDTO = (CategoryDTO) target;

        // Validate that the 'name' field is not empty or whitespace
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required.name", "Category name is a required field and cannot be null or empty.");

        // Validate that 'categoryUrl' should not be sent in a request
        if (categoryDTO.getCategoryUrl() != null) {
            errors.rejectValue("categoryUrl", "invalid.categoryUrl", "Category URL should not be sent as part of the request body.");
        }
    }
}
