package com.blogen.api.v1.controllers;

import com.blogen.api.v1.model.PostDTO;
import com.blogen.api.v1.model.PostListDTO;
import com.blogen.api.v1.model.PostRequestDTO;
import com.blogen.api.v1.services.PostService;
import com.blogen.api.v1.validators.PostRequestDtoValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST Controller for working with {@link com.blogen.domain.Post}
 */
@Tag(name = "Post", description = "Operations on Blogen posts")
@Slf4j
@RestController
@RequestMapping(PostController.BASE_URL)
public class PostController {

    public static final String BASE_URL = "/api/v1/posts";

    private final PostService postService;
    private final PostRequestDtoValidator postRequestDtoValidator;

    @Autowired
    public PostController(PostRequestDtoValidator postRequestDtoValidator, PostService postService) {
        this.postService = postService;
        this.postRequestDtoValidator = postRequestDtoValidator;
    }

    @InitBinder("postRequestDTO")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(postRequestDtoValidator);
    }

    @Operation(summary = "Get a list of parent posts and any child posts belonging to a parent")
    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PostListDTO getPosts(@RequestParam(value = "limit", defaultValue = "5") int limit,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "category", defaultValue = "-1") Long category) {
        log.debug("Fetching posts - page: {}, limit: {}, category: {}", page, limit, category);
        return postService.getPosts(category, page, limit);
    }

    @Operation(summary = "Search posts for the passed in text")
    @GetMapping(value = "/search/{text}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PostListDTO searchPosts(@PathVariable("text") String text,
                                   @RequestParam(value = "limit", defaultValue = "5") int limit) {
        log.debug("Searching posts - limit: {}, text: {}", limit, text);
        return postService.searchPosts(text, limit);
    }

    @Operation(summary = "Get a post by ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PostDTO getPost(@PathVariable("id") Long id) {
        log.debug("Fetching post by ID: {}", id);
        return postService.getPost(id);
    }

    @Operation(summary = "Create a new parent post")
    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDTO createPost(@Valid @RequestBody PostRequestDTO dto) {
        log.debug("Creating new post: {}", dto);
        return postService.createNewPost(dto);
    }

    @Operation(summary = "Create a new child post")
    @PostMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDTO createChildPost(@PathVariable("id") Long parentId,
                                   @Valid @RequestBody PostRequestDTO postRequestDTO) {
        log.debug("Creating new child post - parent ID: {}, post: {}", parentId, postRequestDTO);
        return postService.createNewChildPost(parentId, postRequestDTO);
    }

    @Operation(summary = "Replace an existing post with new post data")
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PostDTO updatePost(@PathVariable("id") Long id, @Valid @RequestBody PostRequestDTO postRequestDTO) {
        log.debug("Updating post - ID: {}, data: {}", id, postRequestDTO);
        return postService.saveUpdatePost(id, postRequestDTO);
    }

    @Operation(summary = "Update field(s) of an existing post")
    @PatchMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public PostDTO patchPost(@PathVariable("id") Long id, @RequestBody PostRequestDTO postRequestDTO) {
        log.debug("Patching post - ID: {}, data: {}", id, postRequestDTO);
        return postService.saveUpdatePost(id, postRequestDTO);
    }

    @Operation(summary = "Delete a post")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(@PathVariable("id") Long id) {
        log.debug("Deleting post by ID: {}", id);
        postService.deletePost(id);
    }
}
