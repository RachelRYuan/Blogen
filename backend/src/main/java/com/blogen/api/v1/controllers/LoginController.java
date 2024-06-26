package com.blogen.api.v1.controllers;

import com.blogen.api.v1.model.LoginRequestDTO;
import com.blogen.api.v1.services.AuthorizationService;
import com.blogen.api.v1.services.oauth2.OAuth2Providers;
import com.blogen.api.v1.services.oauth2.OAuth2UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Controller for handling the two ways users can log in to Blogen:
 * <p>
 * - via Blogen's own HTML login page (using a username and password)
 * - OR via OAuth2 (using GitHub or Google as an OAuth2 authorization service)
 */
@Slf4j
@Controller
@RequestMapping(LoginController.BASE_URL)
public class LoginController {

    public static final String BASE_URL = "/login";

    private final AuthorizationService authorizationService;
    private final OAuth2UserLoginService oAuth2UserLoginService;

    @Autowired
    public LoginController(AuthorizationService authorizationService, OAuth2UserLoginService oAuth2UserLoginService) {
        this.authorizationService = authorizationService;
        this.oAuth2UserLoginService = oAuth2UserLoginService;
    }

    /**
     * Handles successful logins from OAuth2 providers.
     * It performs the final login to Blogen by exchanging the OAuth2User information into a Blogen JSON Web Token (JWT).
     *
     * @param authorizedClient - the authorized client object that performed the OAuth2 login
     * @param oauth2User       - authenticated OAuth2User
     * @param response         - HttpServletResponse
     * @return - a ResponseEntity containing the Blogen index.html page in the body,
     *         and the Blogen JWT in the authorization header; plus a cookie is returned named "token" that
     *         also contains the Blogen JWT, this cookie is set because some browser-based
     *         Single-Page-Applications cannot check for response headers via JavaScript.
     * @throws IOException - if an exception occurs trying to read the index.html page
     */
    @RequestMapping("/oauth2/success")
    public ResponseEntity<String> oauth2SuccessHandler(
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OAuth2User oauth2User,
            HttpServletResponse response) throws IOException {

        log.debug("OAuth2 authenticated principal: {}", authorizedClient.getPrincipalName());
        String providerName = authorizedClient.getClientRegistration().getClientName();
        String token = oAuth2UserLoginService.loginUser(
                OAuth2Providers.valueOf(providerName.toUpperCase()),
                oauth2User);
        return buildLoginResponse(token, response);
    }

    /**
     * Handles logins from the Blogen login form.
     *
     * @param loginDTO - DTO that contains the username and password for login
     * @return - if the login is authenticated, a 200 response is returned, with an
     *         empty body, containing the user's JWT in the Authorization header (as a Bearer Token)
     */
    @PostMapping("/form")
    public ResponseEntity<String> handleFormLogin(@RequestBody @Valid LoginRequestDTO loginDTO) {

        log.debug("LOGIN/form username: {}", loginDTO.getUsername());
        String token = authorizationService.authenticateAndLoginUser(loginDTO.getUsername(), loginDTO.getPassword());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.TEXT_PLAIN)
                .build();
    }

    /**
     * Builds a ResponseEntity containing the main Blogen index.html page in the body,
     * plus an Authorization header containing the Blogen bearer token in JWT format,
     * plus a cookie named "token" containing the JWT.
     *
     * @param bearerToken - the JWT to include in the response header (in compact format)
     * @return a ResponseEntity
     * @throws IOException if the index.html file is not found in the "public" directory
     */
    private ResponseEntity<String> buildLoginResponse(
            String bearerToken,
            HttpServletResponse response) throws IOException {

        File file = new ClassPathResource("public/index.html").getFile();
        String content = new String(Files.readAllBytes(Paths.get(file.getPath())));

        response.addCookie(new Cookie("token", bearerToken));
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + bearerToken)
                .contentType(MediaType.TEXT_HTML)
                .body(content);
    }

}
