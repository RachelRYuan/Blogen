package com.blogen.api.v1.validators;

import com.blogen.api.v1.model.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validate required fields when updating {@link UserDTO}
 */
@Component
public class UpdateUserValidator implements Validator {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 255;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 255;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;

        // Validate first name
        if (hasValue(userDTO.getFirstName()) && isInvalidLength(userDTO.getFirstName(), MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
            errors.rejectValue("firstName", "invalid.firstName", "First name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }

        // Validate last name
        if (hasValue(userDTO.getLastName()) && isInvalidLength(userDTO.getLastName(), MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
            errors.rejectValue("lastName", "invalid.lastName", "Last name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }

        // Validate username
        if (hasValue(userDTO.getUserName()) && isInvalidLength(userDTO.getUserName(), MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
            errors.rejectValue("userName", "invalid.userName", "Username must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }

        // Validate password
        if (hasValue(userDTO.getPassword()) && isInvalidLength(userDTO.getPassword(), MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH)) {
            errors.rejectValue("password", "invalid.password", "Password must be between " + MIN_PASSWORD_LENGTH + " and " + MAX_PASSWORD_LENGTH + " characters.");
        }
    }

    private static boolean hasValue(String data) {
        return data != null && !data.trim().isEmpty();
    }

    private static boolean isInvalidLength(String data, int min, int max) {
        return data.length() < min || data.length() > max;
    }
}