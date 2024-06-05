package com.blogen.repositories;

import com.blogen.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for Post entities.
 * Provides methods to perform CRUD operations and custom queries on Post data.
 * 
 * Author: Cliff
 * Refine: Rachel
 */
public interface PostRepository extends JpaRepository<Post, Long> {

  /**
   * Find all parent posts. Parent posts have a PARENT_ID = NULL in the database.
   * 
   * @return a List containing all parent posts
   */
  List<Post> findAllByParentNull();

  /**
   * Find all parent posts created by a user with the specified user ID.
   * 
   * @param userId the user ID to search for
   * @return a Page of parent posts posted by the specified user ID, ordered by
   *         creation date in descending order
   */
  Page<Post> findAllByUser_IdAndParentNullOrderByCreatedDesc(Long userId, Pageable pageable);

  /**
   * Get a page of parent posts (threads) in descending order of creation.
   * 
   * @param pageable the pagination information
   * @return a Page of parent posts ordered by creation date in descending order
   */
  Page<Post> findAllByParentNullOrderByCreatedDesc(Pageable pageable);

  /**
   * Get a page of posts, parent or child, ordered by creation date.
   * 
   * @param pageable the pagination information
   * @return a Page of posts ordered by creation date in descending order
   */
  Page<Post> findAllByOrderByCreatedDesc(Pageable pageable);

  /**
   * Get a page of posts belonging to the specified category.
   * 
   * @param categoryId the category ID to search for
   * @param pageable   the pagination information
   * @return a Page of posts having the specified category
   */
  Page<Post> findAllByCategory_IdAndParentNull(Long categoryId, Pageable pageable);

  /**
   * Get all parent posts for the specified user ID, using pagination to control
   * the page of results returned.
   * 
   * @param userId   the user ID to search for
   * @param pageable the pagination information
   * @return a Page of posts made by the specified user ID
   */
  Page<Post> findAllByUser_IdAndParentNull(Long userId, Pageable pageable);

  /**
   * Get a page of parent posts for the specified user ID and category ID.
   * 
   * @param userId     the user ID to search for
   * @param categoryId the category ID to search for
   * @param pageable   the pagination information
   * @return a Page of parent posts for the specified user ID and category ID
   */
  Page<Post> findAllByUser_IdAndCategory_IdAndParentNull(Long userId, Long categoryId, Pageable pageable);

  /**
   * Get all posts for a username ordered by creation date.
   * 
   * @param userName the username to search for
   * @return a List of posts made by the specified username, ordered by creation
   *         date in descending order
   */
  List<Post> findAllByUser_userNameOrderByCreatedDesc(String userName);

  /**
   * Searches for the specified string in the text or title of a post.
   * This is a brute-force search using the SQL LIKE operator.
   * 
   * @param searchStr the substring to search for in post text or title
   * @param pageable  the pagination information
   * @return a Page of posts matching the search string
   */
  @Query("select p from Post p where lower(p.title) like %?1% or lower(p.text) like %?1% order by p.created desc")
  Page<Post> findByTextOrTitleIgnoreCaseContaining(String searchStr, Pageable pageable);

  /**
   * Get the 10 most recent posts made.
   * 
   * @return a List of the 10 most recent posts
   */
  List<Post> findTop10ByOrderByCreatedDesc();
}
