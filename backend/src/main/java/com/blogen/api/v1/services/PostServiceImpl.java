package com.blogen.api.v1.services;

import com.blogen.api.v1.controllers.PostController;
import com.blogen.api.v1.mappers.PostMapper;
import com.blogen.api.v1.mappers.PostRequestMapper;
import com.blogen.api.v1.model.PostDTO;
import com.blogen.api.v1.model.PostListDTO;
import com.blogen.api.v1.model.PostRequestDTO;
import com.blogen.domain.Category;
import com.blogen.domain.Post;
import com.blogen.domain.User;
import com.blogen.exceptions.BadRequestException;
import com.blogen.exceptions.NotFoundException;
import com.blogen.repositories.CategoryRepository;
import com.blogen.repositories.PostRepository;
import com.blogen.services.AvatarService;
import com.blogen.services.PrincipalService;
import com.blogen.services.utils.PageRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for performing RESTful CRUD operations on Blogen
 * {@link com.blogen.domain.Post}.
 * 
 * Author: Cliff
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PageRequestBuilder pageRequestBuilder;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final AvatarService avatarService;
    private final PostMapper postMapper;
    private final PostRequestMapper postRequestMapper;
    private final PrincipalService principalService;

    @Autowired
    public PostServiceImpl(PageRequestBuilder pageRequestBuilder, PostRepository postRepository,
            CategoryRepository categoryRepository, UserService userService,
            AvatarService avatarService, PostMapper postMapper,
            PostRequestMapper postRequestMapper, PrincipalService principalService) {
        this.pageRequestBuilder = pageRequestBuilder;
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.avatarService = avatarService;
        this.postMapper = postMapper;
        this.postRequestMapper = postRequestMapper;
        this.principalService = principalService;
    }

    @Override
    public PostListDTO getPosts(Long categoryId, int pageNum, int pageSize) {
        if (categoryId != null && categoryId > -1) {
            validateCategoryId(categoryId);
        }
        PageRequest pageRequest = pageRequestBuilder.buildPageRequest(pageNum, pageSize, Sort.Direction.DESC,
                "created");
        Page<Post> page = (categoryId > -1) ? postRepository.findAllByCategory_IdAndParentNull(categoryId, pageRequest)
                : postRepository.findAllByParentNullOrderByCreatedDesc(pageRequest);

        List<PostDTO> postDTOS = new ArrayList<>();
        page.forEach(post -> postDTOS.add(buildReturnDto(post)));
        return new PostListDTO(postDTOS, PageRequestBuilder.buildPageInfoResponse(page));
    }

    @Override
    public PostDTO getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + id));
        return buildReturnDto(post);
    }

    @Override
    public PostListDTO getPostsForUser(Long userId, Long categoryId, int pageNum, int pageSize) {
        validateUserId(userId);
        if (categoryId != null && categoryId > -1) {
            validateCategoryId(categoryId);
        }
        PageRequest pageRequest = pageRequestBuilder.buildPageRequest(pageNum, pageSize, Sort.Direction.DESC,
                "created");
        Page<Post> page = (categoryId > -1)
                ? postRepository.findAllByUser_IdAndCategory_IdAndParentNull(userId, categoryId, pageRequest)
                : postRepository.findAllByUser_IdAndParentNull(userId, pageRequest);

        List<PostDTO> postDTOS = new ArrayList<>();
        page.forEach(post -> postDTOS.add(buildReturnDto(post)));
        return new PostListDTO(postDTOS, PageRequestBuilder.buildPageInfoResponse(page));
    }

    @Override
    @Transactional
    public PostDTO createNewPost(PostRequestDTO postDTO) {
        Post post = buildNewPost(postDTO);
        Post savedPost = postRepository.save(post);
        return buildReturnDto(savedPost);
    }

    @Override
    @Transactional
    public PostDTO createNewChildPost(Long parentId, PostRequestDTO requestDTO) {
        Post parentPost = postRepository.findById(parentId)
                .orElseThrow(() -> new BadRequestException("Post with id " + parentId + " was not found"));
        if (!parentPost.isParentPost()) {
            throw new BadRequestException("Post with id: " + parentId
                    + " is a child post. Cannot create a new child post onto an existing child post");
        }
        Post childPost = buildNewPost(requestDTO);
        parentPost.addChild(childPost);
        Post savedPost = postRepository.saveAndFlush(parentPost);
        return buildReturnDto(savedPost);
    }

    @Override
    public PostDTO saveUpdatePost(Long id, PostRequestDTO requestDTO) {
        Post postToUpdate = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Post with id " + id + " was not found"));
        validateCategoryId(requestDTO.getCategoryId());
        postRequestMapper.updatePostFromPostRequestDTO(requestDTO, postToUpdate);
        postToUpdate.setCreated(LocalDateTime.now());
        Post savedPost = postRepository.save(postToUpdate);
        return buildReturnDto(savedPost);
    }

    @Override
    public PostListDTO searchPosts(String search, int limit) {
        PageRequest pageRequest = pageRequestBuilder.buildPageRequest(0, limit, Sort.Direction.DESC, "created");
        Page<Post> page = postRepository.findByTextOrTitleIgnoreCaseContaining(search, pageRequest);
        List<PostDTO> postDTOS = new ArrayList<>();
        page.forEach(post -> postDTOS.add(buildReturnDto(post)));
        return new PostListDTO(postDTOS, PageRequestBuilder.buildPageInfoResponse(page));
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Post with id " + id + " was not found"));
        if (!post.isParentPost()) {
            Post parent = post.getParent();
            parent.removeChild(post);
        }
        postRepository.delete(post);
    }

    private Post buildNewPost(PostRequestDTO requestDTO) {
        Long userId = principalService.getPrincipalUserId()
                .orElseThrow(() -> new BadRequestException("JWT user id does not exist in database"));
        Post post = postRequestMapper.postRequestDtoToPost(requestDTO);
        post.setCreated(LocalDateTime.now());
        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new BadRequestException(
                        "Category does not exist with id: " + requestDTO.getCategoryId()));
        User user = userService.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with id " + userId));
        post.setCategory(category);
        post.setUser(user);
        return post;
    }

    private String buildParentPostUrl(Post post) {
        if (!post.isParentPost()) {
            return PostController.BASE_URL + "/" + post.getParent().getId();
        }
        return null;
    }

    private PostDTO buildReturnDto(Post post) {
        PostDTO postDTO = postMapper.postToPostDto(post);
        postDTO.setPostUrl(buildPostUrl(post));
        postDTO.getUser().setAvatarUrl(avatarService.buildAvatarUrl(post.getUser()));
        if (post.getParent() != null) {
            postDTO.setParentPostUrl(buildPostUrl(post.getParent()));
        }
        if (post.getChildren() != null) {
            for (int i = 0; i < post.getChildren().size(); i++) {
                PostDTO childDTO = postDTO.getChildren().get(i);
                Post child = post.getChildren().get(i);
                childDTO.setPostUrl(buildPostUrl(child));
                childDTO.getUser().setAvatarUrl(avatarService.buildAvatarUrl(child.getUser()));
                childDTO.setParentPostUrl(buildParentPostUrl(child));
            }
        }
        return postDTO;
    }

    private void validateCategoryId(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id: " + id + " does not exist"));
    }

    private void validateUserId(Long id) {
        userService.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " does not exist"));
    }
}
