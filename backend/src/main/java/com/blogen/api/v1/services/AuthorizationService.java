package com.blogen.api.v1.services;

import com.blogen.api.v1.model.LoginResponse;
import com.blogen.api.v1.model.LoginRequestDTO;
import com.blogen.api.v1.model.UserDTO;

/**
 * Service for new user sign-up and user log-ins via the Blogen sign-up page or
 * via OAuth2 providers (GitHub and Google).
 * 
 * Author: Cliff
 */
public interface AuthorizationService {

    /**
     * Sign up a new user.
     * 
     * @param userDTO The user data transfer object containing user details.
     * @return UserDTO containing the data that was saved to the database.
     */
    UserDTO signUpUser(UserDTO userDTO);

    /**
     * Authenticate and log in a user using their username and password.
     * If authenticated, generate a JSON Web Token for the user.
     * The JSON Web Token must be sent in the header when the user
     * tries to access a protected resource (mainly the REST API).
     * 
     * @param loginRequestDTO The login request data transfer object containing login details.
     * @return LoginResponse containing the authenticated user's JSON Web Token.
     */
    LoginResponse authenticateAndLoginUser(LoginRequestDTO loginRequestDTO);

    /**
     * Authenticate and log in a user using their username and password.
     * If authenticated, generate a JSON Web Token for the user.
     * 
     * @param username The user's username.
     * @param password The user's password.
     * @return A string representation of the authenticated user's JSON Web Token.
     */
    String authenticateAndLoginUser(String username, String password);

    /**
     * Check if the given username exists in the User table.
     * 
     * @param userName The username to search for.
     * @return true if the username exists, else false.
     */
    Boolean userNameExists(String userName);
}
