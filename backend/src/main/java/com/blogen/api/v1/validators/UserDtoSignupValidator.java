package com.blogen.api.v1.validators;

import com.blogen.api.v1.model.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validate required fields of a {@link UserDTO} for sign-up
 */
@Component
public class UserDtoSignupValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;

        // Validate required fields for sign-up
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "required.firstName", "First name is a required field and cannot be null or empty.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "required.lastName", "Last name is a required field and cannot be null or empty.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "required.userName", "Username is a required field and cannot be null or empty.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "required.email", "Email is a required field and cannot be null or empty.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password", "Password is a required field and cannot be null or empty.");
    }
}