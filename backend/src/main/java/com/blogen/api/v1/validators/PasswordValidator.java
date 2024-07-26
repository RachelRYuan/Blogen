package com.blogen.api.v1.validators;

import com.blogen.api.v1.model.PasswordRequestDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validate required fields when PUTing {@link com.blogen.api.v1.model.PasswordRequestDTO}
 */
@Component
public class PasswordValidator implements Validator {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 255;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordRequestDTO dto = (PasswordRequestDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password", "Password is a required field and cannot be null or empty.");

        if (isInvalidLength(dto.getPassword(), MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH)) {
            errors.rejectValue("password", "invalid.password", "Password must be between " + MIN_PASSWORD_LENGTH + " and " + MAX_PASSWORD_LENGTH + " characters.");
        }
    }

    private static boolean isInvalidLength(String data, int min, int max) {
        return data == null || data.length() < min || data.length() > max;
    }
}
