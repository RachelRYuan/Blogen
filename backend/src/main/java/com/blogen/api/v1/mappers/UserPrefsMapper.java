package com.blogen.api.v1.mappers;

import com.blogen.api.v1.model.UserPrefsDTO;
import com.blogen.domain.UserPrefs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mappers for mapping between {@link UserPrefs} and {@link UserPrefsDTO}.
 * <p>
 * This interface is implemented automatically by MapStruct at compile time.
 * </p>
 * <p>
 * Usage Example:
 * <pre>
 * {@code
 * UserPrefsMapper mapper = UserPrefsMapper.INSTANCE;
 * UserPrefsDTO userPrefsDTO = mapper.userPrefsToUserPrefsDto(userPrefs);
 * UserPrefs userPrefs = mapper.userPrefsDtoToUserPrefs(userPrefsDTO);
 * }
 * </pre>
 * </p>
 * </p>
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserPrefsMapper {

    UserPrefsMapper INSTANCE = Mappers.getMapper(UserPrefsMapper.class);

    // Map UserPrefs to UserPrefsDTO
    @Mapping(target = "avatarImage", source = "avatar.fileName")
    UserPrefsDTO userPrefsToUserPrefsDto(UserPrefs userPrefs);

    // Map UserPrefsDTO to UserPrefs
    @Mapping(target = "avatar.fileName", source = "avatarImage")
    UserPrefs userPrefsDtoToUserPrefs(UserPrefsDTO userPrefsDTO);
}