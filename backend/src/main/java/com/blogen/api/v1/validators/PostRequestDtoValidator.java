package com.blogen.api.v1.validators;

import com.blogen.api.v1.model.PostRequestDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validate required fields of a {@link com.blogen.api.v1.model.PostRequestDTO}
 */
@Component
public class PostRequestDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PostRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PostRequestDTO postDTO = (PostRequestDTO) target;

        // Validate required fields
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "required.title", "Title is a required field and cannot be null or empty.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "text", "required.text", "Text is a required field and cannot be null or empty.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "categoryId", "required.categoryId", "Category ID is a required field and cannot be null or empty.");
    }
}