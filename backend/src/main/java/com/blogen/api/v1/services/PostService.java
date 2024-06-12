package com.blogen.api.v1.services;

import com.blogen.api.v1.controllers.PostController;
import com.blogen.api.v1.model.PostDTO;
import com.blogen.api.v1.model.PostListDTO;
import com.blogen.api.v1.model.PostRequestDTO;
import com.blogen.domain.Post;

/**
 * Service interface for REST methods that operate on Blogen
 * {@link com.blogen.domain.Post}(s).
 * 
 * Author: Cliff
 */
public interface PostService {

    /**
     * Get all posts containing the specified categoryId, for the specified pageNum,
     * with up to pageSize posts per page.
     * 
     * @param categoryId categoryId of posts to retrieve, set to -1 to get all posts
     *                   in all categories.
     * @param pageNum    the page number of posts to retrieve.
     * @param pageSize   the number of posts per page to retrieve.
     * @return A PostListDTO containing the posts.
     */
    PostListDTO getPosts(Long categoryId, int pageNum, int pageSize);

    /**
     * Get a specific post by its ID.
     *
     * @param id The id of the post to retrieve. If the id refers to a parent post,
     *           then the parent post and its children will be returned. If the ID
     *           refers to a child post, then only that child post will be returned.
     * @return A PostDTO representing the post.
     */
    PostDTO getPost(Long id);

    /**
     * Get posts for the specified user and category.
     * 
     * @param userId     The ID of the user whose posts are to be retrieved.
     * @param categoryId The categoryId of the posts to retrieve.
     * @param pageNum    The page number of posts to return.
     * @param limit      The maximum number of posts per page to return.
     * @return A PostListDTO containing the user's posts.
     */
    PostListDTO getPostsForUser(Long userId, Long categoryId, int pageNum, int limit);

    /**
     * Create a new Parent Post. Any PostDTO.children sent will be ignored.
     *
     * @param requestDTO Contains post data to create.
     * @return A PostDTO representing the newly created post.
     */
    PostDTO createNewPost(PostRequestDTO requestDTO);

    /**
     * Create a new child post. The child post will be associated with the Parent
     * Post represented by the parentId.
     * 
     * @param parentId   The ID of the parent post, under which this child will be
     *                   created.
     * @param requestDTO Contains post data to create.
     * @return A PostDTO representing the newly created child post.
     */
    PostDTO createNewChildPost(Long parentId, PostRequestDTO requestDTO);

    /**
     * Save or update an existing Post.
     * 
     * @param id         The ID of the Post to update.
     * @param requestDTO Contains data to update the post.
     * @return A PostDTO containing the newly updated fields.
     */
    PostDTO saveUpdatePost(Long id, PostRequestDTO requestDTO);

    /**
     * Search posts title and text for the specified search string.
     * 
     * @param search The text string to search for.
     * @param limit  The maximum number of posts to return.
     * @return A PostListDTO containing the search results.
     */
    PostListDTO searchPosts(String search, int limit);

    /**
     * Delete the post with the specified ID.
     * 
     * @param id The ID of the post to delete.
     */
    void deletePost(Long id);

    /**
     * Helper method that builds a URL string to a particular post.
     * 
     * @param post The post for which to build the URL.
     * @return The URL string to the post.
     */
    default String buildPostUrl(Post post) {
        return PostController.BASE_URL + "/" + post.getId();
    }
}
