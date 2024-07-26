package com.blogen.api.v1.mappers;

import com.blogen.api.v1.model.UserDTO;
import com.blogen.api.v1.services.UserService;
import com.blogen.domain.Role;
import com.blogen.domain.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * MapStruct mappers for mapping between {@link User} and {@link UserDTO}.
 * <p>
 * This interface is implemented automatically by MapStruct at compile time.
 * </p>
 * <p>
 * It ensures that source properties that are NULL don't get mapped onto target properties.
 * </p>
 * <p>
 * Usage Example:
 * <pre>
 * {@code
 * UserMapper mapper = UserMapper.INSTANCE;
 * UserDTO userDTO = mapper.userToUserDto(user);
 * User user = mapper.userDtoToUser(userDTO);
 * mapper.updateUserFromDTO(userDTO, user);
 * }
 * </pre>
 * </p>
 * </p>
 * </p>
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Map User to UserDTO with custom mappings
    @Mapping(target = "avatarImage", source = "userPrefs.avatar.fileName")
    @Mapping(target = "userUrl", expression = "java(com.blogen.api.v1.services.UserService.buildUserUrl(user))")
    @Mapping(target = "password", constant = "")
    UserDTO userToUserDto(User user);

    // Map UserDTO to User
    @Mapping(target = "userPrefs.avatar.fileName", source = "avatarImage")
    User userDtoToUser(UserDTO userDTO);

    // Update User entity with data from UserDTO
    @Mapping(source = "avatarImage", target = "userPrefs.avatar.fileName")
    void updateUserFromDTO(UserDTO userDTO, @MappingTarget User user);

    // Custom mapping from List<Role> to List<String>
    default List<String> rolesToStrings(List<Role> roles) {
        if (roles == null) {
            return null;
        }
        List<String> strings = new ArrayList<>();
        for (Role role : roles) {
            strings.add(role.getRole());
        }
        return strings;
    }

    // Custom mapping from List<String> to List<Role>
    default List<Role> stringsToRoles(List<String> strings) {
        List<Role> roles = new ArrayList<>();
        if (strings != null) {
            strings.forEach(s -> {
                Role r = new Role();
                r.setRole(s);
                roles.add(r);
            });
        }
        return roles;
    }
}