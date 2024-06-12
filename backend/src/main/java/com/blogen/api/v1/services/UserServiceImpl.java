package com.blogen.api.v1.services;

import com.blogen.api.v1.mappers.UserMapper;
import com.blogen.api.v1.model.PasswordRequestDTO;
import com.blogen.api.v1.model.UserDTO;
import com.blogen.api.v1.model.UserListDTO;
import com.blogen.domain.Avatar;
import com.blogen.domain.Role;
import com.blogen.domain.User;
import com.blogen.domain.UserPrefs;
import com.blogen.exceptions.BadRequestException;
import com.blogen.repositories.UserRepository;
import com.blogen.services.AvatarService;
import com.blogen.services.RoleService;
import com.blogen.services.security.PasswordEncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for RESTful operations on Blogen {@link com.blogen.domain.User}.
 * 
 * Author: Cliff
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final PasswordEncryptionService encryptionService;
    private final RoleService roleService;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           AvatarService avatarService,
                           PasswordEncryptionService encryptionService,
                           RoleService roleService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.avatarService = avatarService;
        this.encryptionService = encryptionService;
        this.roleService = roleService;
        this.userMapper = userMapper;
    }

    @Override
    public UserListDTO getAllUsers() {
        List<UserDTO> userDTOS = userRepository.findAll().stream()
                .map(user -> {
                    UserDTO dto = userMapper.userToUserDto(user);
                    dto.setUserUrl(UserService.buildUserUrl(user));
                    return dto;
                }).collect(Collectors.toList());
        return new UserListDTO(userDTOS);
    }

    @Override
    public UserDTO getUser(Long id) {
        User user = validateUserId(id);
        UserDTO userDTO = userMapper.userToUserDto(user);
        userDTO.setUserUrl(UserService.buildUserUrl(user));
        return userDTO;
    }

    @Override
    public User createNewUser(UserDTO userDTO) throws IllegalArgumentException {
        if (findByUserName(userDTO.getUserName()).isPresent()) {
            throw new IllegalArgumentException("User with userName=" + userDTO.getUserName() + " already exists");
        } else {
            User user = userMapper.userDtoToUser(userDTO);
            user.setEncryptedPassword(encryptionService.encrypt(user.getPassword()));
            user.addRole(roleService.getByName("ROLE_USER"));
            user.addRole(roleService.getByName("ROLE_API"));
            user.setUserPrefs(buildDefaultUserPrefs());
            return saveUser(user);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') || #user.getId().toString() == authentication.name")
    public UserDTO updateUser(User user, UserDTO userDTO) {
        userMapper.updateUserFromDTO(userDTO, user);
        if (userDTO.getAvatarImage() != null) {
            Avatar avatar = avatarService.getAvatarByFileName(userDTO.getAvatarImage())
                    .orElseThrow(() -> new BadRequestException("Avatar image does not exist: " + userDTO.getAvatarImage()));
            user.getUserPrefs().setAvatar(avatar);
        }
        User savedUser = userRepository.save(user);
        UserDTO returnDto = userMapper.userToUserDto(savedUser);
        returnDto.setUserUrl(UserService.buildUserUrl(savedUser));
        return returnDto;
    }

    @Override
    public User saveUser(User user) {
        checkAndEncryptPassword(user);
        return userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN') || #user.getId().toString() == authentication.name")
    @Transactional
    public void changePassword(User user, PasswordRequestDTO dto) {
        user.setPassword(dto.getPassword());
        checkAndEncryptPassword(user);
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUserName(String name) {
        return Optional.ofNullable(userRepository.findByUserName(name));
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Encrypts the user password if it was set on the User object.
     *
     * @param user The user whose password needs to be encrypted.
     */
    private void checkAndEncryptPassword(User user) {
        if (user.getPassword() != null) {
            user.setEncryptedPassword(encryptionService.encrypt(user.getPassword()));
        }
    }

    /**
     * Validates that the passed-in user ID exists in the repository.
     *
     * @param id The user ID to validate.
     * @return The User corresponding to the passed-in ID.
     * @throws BadRequestException if the user was not found in the repository.
     */
    private User validateUserId(Long id) throws BadRequestException {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User does not exist with id: " + id));
    }

    /**
     * Builds a default user preferences object with the default avatar image.
     *
     * @return A UserPrefs object.
     */
    public UserPrefs buildDefaultUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        Avatar defaultAvatar = avatarService.getAvatarByFileName(AvatarService.DEFAULT_AVATAR)
                .orElseThrow(() -> new BadRequestException("Default avatar image not found"));
        userPrefs.setAvatar(defaultAvatar);
        return userPrefs;
    }
}
