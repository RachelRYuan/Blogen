package com.blogen.api.v1.controllers;

import com.blogen.api.v1.model.PasswordRequestDTO;
import com.blogen.api.v1.model.PostListDTO;
import com.blogen.api.v1.model.UserDTO;
import com.blogen.api.v1.model.UserListDTO;
import com.blogen.api.v1.services.PostService;
import com.blogen.api.v1.services.UserService;
import com.blogen.api.v1.validators.PasswordValidator;
import com.blogen.api.v1.validators.UpdateUserValidator;
import com.blogen.domain.User;
import com.blogen.exceptions.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller for REST operations in Blogen {@link com.blogen.domain.User}
 */
@Tag(name = "User", description = "Operations on Blogen users")
@Slf4j
@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {

    public static final String BASE_URL = "/api/v1/users";

    private final UserService userService;
    private final PostService postService;
    private final UpdateUserValidator updateUserValidator;
    private final PasswordValidator passwordValidator;

    @Autowired
    public UserController(UserService userService,
                          PostService postService,
                          UpdateUserValidator updateUserValidator,
                          PasswordValidator passwordValidator) {
        this.userService = userService;
        this.postService = postService;
        this.updateUserValidator = updateUserValidator;
        this.passwordValidator = passwordValidator;
    }

    @InitBinder("userDTO")
    public void setupUpdateUserBinder(WebDataBinder binder) {
        binder.addValidators(updateUserValidator);
    }

    @InitBinder("passwordRequestDTO")
    public void setupPasswordBinder(WebDataBinder binder) {
        binder.addValidators(passwordValidator);
    }

    @GetMapping("/authenticate")
    @ResponseBody
    public UserDTO getAuthenticatedUserInfo(Authentication authentication) {
        log.debug("Get user info for: {}", authentication.getName());
        return userService.getUser(Long.parseLong(authentication.getName()));
    }

    @Operation(summary = "Get a list of all users")
    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public UserListDTO getAllUsers() {
        log.debug("Getting all users");
        return userService.getAllUsers();
    }

    @Operation(summary = "Get a specific user by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUser(@PathVariable("id") Long id) {
        log.debug("Get user by ID: {}", id);
        return userService.getUser(id);
    }

    @Operation(summary = "Get posts made by a user")
    @GetMapping(value = "/{id}/posts", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PostListDTO getUserPosts(@PathVariable("id") Long id,
                                    @RequestParam(value = "page", defaultValue = "0") Integer page,
                                    @RequestParam(value = "limit", defaultValue = "5") Integer limit,
                                    @RequestParam(value = "category", defaultValue = "-1") Long category) {
        log.debug("Get user posts - ID: {}, page: {}, limit: {}, category: {}", id, page, limit, category);
        return postService.getPostsForUser(id, category, page, limit);
    }

    @Operation(summary = "Update field(s) of an existing user")
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUser(@PathVariable("id") Long id,
                              @Valid @RequestBody UserDTO userDTO) {
        log.debug("Update user - ID: {}, userDTO: {}", id, userDTO);
        User user = userService.findById(id)
                .orElseThrow(() -> new BadRequestException("User does not exist with ID: " + id));
        return userService.updateUser(user, userDTO);
    }

    @Operation(summary = "Change a user's password")
    @PutMapping(value = "/{id}/password", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@PathVariable("id") Long id,
                               @Valid @RequestBody PasswordRequestDTO passwordRequestDTO) {
        log.debug("Change password - user ID: {}, passwordDTO: {}", id, passwordRequestDTO);
        User user = userService.findById(id)
                .orElseThrow(() -> new BadRequestException("User does not exist with ID: " + id));
        userService.changePassword(user, passwordRequestDTO);
    }
}
