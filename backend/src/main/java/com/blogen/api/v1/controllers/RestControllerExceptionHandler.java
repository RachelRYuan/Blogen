package com.blogen.api.v1.controllers;

import com.blogen.api.v1.model.ApiErrorsView;
import com.blogen.api.v1.model.ApiFieldError;
import com.blogen.api.v1.model.ApiGlobalError;
import com.blogen.exceptions.BadRequestException;
import com.blogen.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Exception Handlers for REST Controllers
 */
@Slf4j
@ControllerAdvice("com.blogen.api.v1.controllers")
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ NotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(Exception exception, WebRequest request) {
        log.error("NotFoundException: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({ BadRequestException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBadRequestException(Exception exception, WebRequest request) {
        log.error("BadRequestException: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({ BadCredentialsException.class, AccessDeniedException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleUnauthorizedException(Exception exception, WebRequest request) {
        log.error("UnauthorizedException: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleDataIntegrityViolationException(Exception exception, WebRequest request) {
        log.error("DataIntegrityViolationException: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("TypeMismatchException: {}", ex.getMessage());
        String methodParamName = (ex instanceof MethodArgumentTypeMismatchException)
                ? ((MethodArgumentTypeMismatchException) ex).getName()
                : "";
        String errorMessage = String.format("Invalid type for parameter: %s with value: %s", methodParamName, ex.getValue());
        return buildResponseEntity(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("MethodArgumentNotValidException: {}", exception.getMessage());
        BindingResult bindingResult = exception.getBindingResult();

        List<ApiFieldError> apiFieldErrors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> new ApiFieldError(fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue()))
                .collect(toList());

        List<ApiGlobalError> apiGlobalErrors = bindingResult.getGlobalErrors().stream()
                .map(globalError -> new ApiGlobalError(globalError.getCode()))
                .collect(toList());

        ApiErrorsView apiErrorsView = new ApiErrorsView(apiFieldErrors, apiGlobalErrors);
        return new ResponseEntity<>(apiErrorsView, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleOtherExceptions(Exception exception) {
        log.error("Exception: {}", exception.getMessage());
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message) {
        ApiGlobalError globalError = new ApiGlobalError(message);
        List<ApiGlobalError> globalErrors = Arrays.asList(globalError);
        ApiErrorsView errorsView = new ApiErrorsView(null, globalErrors);
        return new ResponseEntity<>(errorsView, new HttpHeaders(), status);
    }
}
