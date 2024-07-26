package com.blogen.api.v1.mappers;

import com.blogen.api.v1.model.PostUserDTO;
import com.blogen.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Utilizes MapStruct to map between Blogen {@link User} and {@link PostUserDTO}.
 * <p>
 * This interface is implemented automatically by MapStruct at compile time.
 * </p>
 * <p>
 * Usage Example:
 * <pre>
 * {@code
 * PostUserMapper mapper = PostUserMapper.INSTANCE;
 * PostUserDTO postUserDTO = mapper.userToPostUserDto(user);
 * User user = mapper.postUserDtoToUser(postUserDTO);
 * }
 * </pre>
 * </p>
 * </p>
 * </p>
 */
@Mapper(componentModel = "spring")
public interface PostUserMapper {

    PostUserMapper INSTANCE = Mappers.getMapper(PostUserMapper.class);

    // Map User to PostUserDTO with custom URL mapping
    @Mapping(target = "userUrl", expression = "java(com.blogen.api.v1.services.UserService.buildUserUrl(user))")
    PostUserDTO userToPostUserDto(User user);

    // Map PostUserDTO to User
    User postUserDtoToUser(PostUserDTO postUserDTO);
}