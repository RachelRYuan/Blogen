package com.blogen.api.v1.controllers;

import com.blogen.api.v1.model.AvatarResponse;
import com.blogen.services.AvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for retrieving user preferences
 */
@Tag(name = "UserPreferences", description = "Operations on user avatar file names")
@Slf4j
@RestController
@RequestMapping(UserPrefsController.BASE_URL)
public class UserPrefsController {

    public static final String BASE_URL = "/api/v1/userPrefs";

    private final AvatarService avatarService;

    @Autowired
    public UserPrefsController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    /**
     * Returns a list of all Avatar file names.
     *
     * @return an AvatarResponse containing a list of avatar image names.
     */
    @Operation(summary = "Returns a list of all Avatar file names")
    @GetMapping(value = "/avatars", produces = "application/json")
    public AvatarResponse avatarImageFileNames() {
        log.debug("Fetching all avatar image file names");
        return AvatarResponse.builder()
                .avatars(avatarService.getAllAvatarImageNames())
                .build();
    }
}
