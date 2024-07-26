package com.blogen.api.v1.mappers;
import com.blogen.api.v1.model.CategoryDTO;
import com.blogen.api.v1.model.PostDTO;
import com.blogen.api.v1.model.PostUserDTO;
import com.blogen.api.v1.services.UserService;
import com.blogen.domain.Category;
import com.blogen.domain.Post;
import com.blogen.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mappers for mapping data between {@link com.blogen.domain.Post} and {@link com.blogen.api.v1.model.PostDTO}
 * as well as related entities such as {@link com.blogen.domain.Category} and {@link com.blogen.domain.User}.
 * <p>
 * This interface is implemented automatically by MapStruct at compile time.
 * </p>
 * <p>
 * Usage Example:
 * <pre>
 * {@code
 * PostMapper mapper = PostMapper.INSTANCE;
 * PostDTO postDTO = mapper.postToPostDto(post);
 * Post post = mapper.postDtoToPost(postDTO);
 * }
 * </pre>
 * </p>
 * </p>
 * </p>
 * </p>
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    // Map Post to PostDTO
    PostDTO postToPostDto(Post post);

    // Map PostDTO to Post
    Post postDtoToPost(PostDTO postDTO);

    // Map Category to CategoryDTO with custom URL mapping
    @Mapping(target = "categoryUrl", expression = "java(com.blogen.api.v1.services.CategoryService.buildCategoryUrl(category))")
    CategoryDTO categoryToCategoryDto(Category category);

    // Map CategoryDTO to Category
    Category categoryDtoToCategory(CategoryDTO categoryDTO);

    // Custom mapping from User to PostUserDTO
    default PostUserDTO userToPostUserDto(User user) {
        PostUserDTO postUserDTO = new PostUserDTO();
        postUserDTO.setId(user.getId());
        postUserDTO.setUserName(user.getUserName());
        postUserDTO.setUserUrl(UserService.buildUserUrl(user));
        // TODO: Should relative URL be passed instead of filename?
        postUserDTO.setAvatarUrl(user.getUserPrefs().getAvatar().getFileName());
        return postUserDTO;
    }

    // Map PostUserDTO to User
    User postUserDtoToUser(PostUserDTO postUserDTO);
}