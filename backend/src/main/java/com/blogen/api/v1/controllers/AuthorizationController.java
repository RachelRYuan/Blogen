package com.blogen.api.v1.controllers;

import com.blogen.api.v1.model.PostListDTO;
import com.blogen.api.v1.model.UserDTO;
import com.blogen.api.v1.services.AuthorizationService;
import com.blogen.api.v1.services.PostService;
import com.blogen.api.v1.validators.UserDtoSignupValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller for handling endpoints that do NOT require users to be authenticated and authorized,
 * such as new-user sign-ups, checking if a username exists, etc.
 */
@Tag(name = "Authorization", description = "Operations for logging in users")
@Slf4j
@RestController
@RequestMapping(AuthorizationController.BASE_URL)
public class AuthorizationController {

    public static final String BASE_URL = "/api/v1/auth";

    private final AuthorizationService authorizationService;
    private final UserDtoSignupValidator userSignupValidator;
    private final PostService postService;

    @Autowired
    public AuthorizationController(AuthorizationService authorizationService,
                                   UserDtoSignupValidator userSignupValidator,
                                   PostService postService) {
        this.authorizationService = authorizationService;
        this.userSignupValidator = userSignupValidator;
        this.postService = postService;
    }

    @InitBinder("userDTO")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(userSignupValidator);
    }

    @SecurityRequirements
    @Operation(summary = "Sign up a new user")
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO signupUser(@RequestBody @Valid UserDTO userDTO) {
        log.debug("Signing up user: {}", userDTO);
        return authorizationService.signUpUser(userDTO);
    }

    @SecurityRequirements
    @Operation(summary = "Get the latest posts")
    @GetMapping(value = "/latestPosts", produces = MediaType.APPLICATION_JSON_VALUE)
    public PostListDTO latestPosts(@RequestParam(name = "limit", defaultValue = "9") int limit) {
        log.debug("Fetching latest posts with limit: {}", limit);
        return postService.getPosts(-1L, 0, limit);
    }

    @SecurityRequirements
    @Operation(summary = "Check if a username exists")
    @GetMapping(value = "/username/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Boolean userNameExists(@PathVariable("name") String userName) {
        Boolean userExists = authorizationService.userNameExists(userName);
        log.debug("Checking if username exists: {} - {}", userName, userExists);
        return userExists;
    }
}
