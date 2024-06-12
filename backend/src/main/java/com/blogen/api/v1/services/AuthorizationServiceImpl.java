package com.blogen.api.v1.services;

import com.blogen.api.v1.mappers.UserMapper;
import com.blogen.api.v1.model.LoginRequestDTO;
import com.blogen.api.v1.model.LoginResponse;
import com.blogen.api.v1.model.UserDTO;
import com.blogen.domain.User;
import com.blogen.exceptions.BadRequestException;
import com.blogen.services.security.BlogenAuthority;
import com.blogen.services.security.BlogenJwtService;
import com.blogen.services.security.PasswordEncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for signing up new users and logging in existing users.
 */
@Service
@Slf4j
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final String DEFAULT_AVATAR_IMAGE = "avatar0.jpg";

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncryptionService passwordEncryptionService;
    private final BlogenJwtService jwtService;

    public AuthorizationServiceImpl(UserService userService,
                                    UserMapper userMapper,
                                    PasswordEncryptionService passwordEncryptionService,
                                    BlogenJwtService jwtService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncryptionService = passwordEncryptionService;
        this.jwtService = jwtService;
    }

    @Override
    public Boolean userNameExists(String userName) {
        return userService.findByUserName(userName).isPresent();
    }

    @Override
    public UserDTO signUpUser(UserDTO userDTO) {
        try {
            User newUser = userService.createNewUser(userDTO);
            UserDTO returnDto = userMapper.userToUserDto(newUser);
            returnDto.setUserUrl(UserService.buildUserUrl(newUser));
            return returnDto;
        } catch (IllegalArgumentException e) {
            log.error("User with userName={} already exists", userDTO.getUserName(), e);
            throw new BadRequestException("User with userName=" + userDTO.getUserName() + " already exists");
        }
    }

    @Override
    public LoginResponse authenticateAndLoginUser(LoginRequestDTO loginDTO) {
        User user = validateUserCredentials(loginDTO.getUsername(), loginDTO.getPassword());
        String token = buildJwt(user);
        return new LoginResponse(token, userMapper.userToUserDto(user));
    }

    @Override
    public String authenticateAndLoginUser(String username, String password) {
        User user = validateUserCredentials(username, password);
        return buildJwt(user);
    }

    /**
     * Validates the user credentials.
     *
     * @param username The user's username.
     * @param password The user's password.
     * @return The authenticated user.
     * @throws BadCredentialsException If the username or password is incorrect.
     */
    private User validateUserCredentials(String username, String password) {
        User user = userService.findByUserName(username)
                .orElseThrow(() -> new BadCredentialsException("Bad username or password"));
        if (!passwordEncryptionService.checkPassword(password, user.getEncryptedPassword())) {
            throw new BadCredentialsException("Bad username or password");
        }
        return user;
    }

    /**
     * Builds a JWT from Blogen user data. The JWT will include a "scope" claim
     * containing the user's granted authorities.
     *
     * @param user The Blogen {@link User} data.
     * @return A JWT in compact form (BASE64 encoded).
     */
    private String buildJwt(User user) {
        List<BlogenAuthority> scopes = user.getRoles().stream()
                .map(role -> BlogenAuthority.valueOf(role.getRole().toUpperCase()))
                .collect(Collectors.toList());

        return jwtService.generateToken(Long.toString(user.getId()), scopes, null, null);
    }
}
