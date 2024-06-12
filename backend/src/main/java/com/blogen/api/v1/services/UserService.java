package com.blogen.api.v1.services;

import com.blogen.api.v1.controllers.UserController;
import com.blogen.api.v1.model.PasswordRequestDTO;
import com.blogen.api.v1.model.UserDTO;
import com.blogen.api.v1.model.UserListDTO;
import com.blogen.domain.User;
import com.blogen.domain.UserPrefs;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for REST operations on {@link com.blogen.domain.User}.
 * 
 * Author: Cliff
 */
public interface UserService {

    /**
     * Fetch a list of all users.
     *
     * @return A UserListDTO containing a list of {@link UserDTO}.
     */
    UserListDTO getAllUsers();

    /**
     * Fetch a {@link User} by their ID.
     *
     * @param id The database ID of the user.
     * @return A UserDTO representing the user.
     */
    UserDTO getUser(Long id);

    /**
     * Create a new Blogen user from the data in the userDTO and save it into the database.
     * The UserDTO.id field will be ignored as a new ID will be generated.
     *
     * @param userDTO The data transfer object containing user details.
     * @return A new Blogen User with all fields set.
     * @throws IllegalArgumentException If the username already exists.
     */
    User createNewUser(UserDTO userDTO) throws IllegalArgumentException;

    /**
     * Update user fields using fields in the userDTO.
     *
     * @param user    The existing User to update.
     * @param userDTO The data transfer object containing user fields to update.
     * @return A UserDTO containing the user's updated information.
     */
    UserDTO updateUser(User user, UserDTO userDTO);

    /**
     * Fetch a {@link User} from the repository by their username.
     *
     * @param name The username.
     * @return An Optional containing the User, if found.
     */
    Optional<User> findByUserName(String name);

    /**
     * Fetch a {@link User} from the repository by their ID.
     *
     * @param id The ID of the user.
     * @return An Optional containing the User, if found.
     */
    Optional<User> findById(Long id);

    /**
     * Save a {@link User} into the repository.
     *
     * @param user The {@link User} to save.
     * @return The saved {@link User}.
     */
    User saveUser(User user);

    /**
     * Change a user's password.
     *
     * @param user                The {@link User} whose password will be changed.
     * @param passwordRequestDTO The data transfer object containing the new password.
     */
    void changePassword(User user, PasswordRequestDTO passwordRequestDTO);

    /**
     * Build a HATEOAS style URL that identifies a specific user.
     *
     * @param user The {@link User} object used to build the user URL.
     * @return A relative URL to the specified user.
     */
    static String buildUserUrl(User user) {
        return UserController.BASE_URL + "/" + user.getId();
    }

    /**
     * Build default user preferences.
     *
     * @return A {@link UserPrefs} object with default preferences.
     */
    UserPrefs buildDefaultUserPrefs();
}
