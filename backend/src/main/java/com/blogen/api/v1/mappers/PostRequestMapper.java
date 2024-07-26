package com.blogen.api.v1.mappers;

import com.blogen.api.v1.model.PostRequestDTO;
import com.blogen.domain.Post;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mappers for mapping data between {@link Post} and {@link PostRequestDTO}.
 * <p>
 * This interface is implemented automatically by MapStruct at compile time.
 * </p>
 * <p>
 * It ensures that source properties that are null are not mapped onto target properties.
 * </p>
 * <p>
 * Usage Example:
 * <pre>
 * {@code
 * PostRequestMapper mapper = PostRequestMapper.INSTANCE;
 * PostRequestDTO postRequestDTO = mapper.postToPostRequestDto(post);
 * Post post = mapper.postRequestDtoToPost(postRequestDTO);
 * mapper.updatePostFromPostRequestDTO(postRequestDTO, post);
 * }
 * </pre>
 * </p>
 * </p>
 * </p>
 */
@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostRequestMapper {

    PostRequestMapper INSTANCE = Mappers.getMapper(PostRequestMapper.class);

    // Map Post to PostRequestDTO
    @Mapping(target = "categoryId", source = "category.id")
    PostRequestDTO postToPostRequestDto(Post post);

    // Map PostRequestDTO to Post
    @Mapping(target = "category.id", source = "categoryId")
    Post postRequestDtoToPost(PostRequestDTO postDTO);

    /**
     * Updates the given post object with data from the given PostRequestDTO.
     *
     * @param requestDTO the PostRequestDTO containing the updated data
     * @param post the Post entity to be updated
     */
    void updatePostFromPostRequestDTO(PostRequestDTO requestDTO, @MappingTarget Post post);
}