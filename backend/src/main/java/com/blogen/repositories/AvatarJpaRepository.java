package com.blogen.repositories;

import com.blogen.domain.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA implementation of the Avatar repository.
 * 
 * This interface extends JpaRepository and AvatarRepository to provide CRUD
 * operations and custom query methods
 * for the Avatar entity.
 * 
 * Note: This repository is not currently used in the project, but is left here
 * for reference purposes.
 * 
 * Author: Cliff
 * Refine: Rachel
 */
public interface AvatarJpaRepository extends JpaRepository<Avatar, Long>, AvatarRepository {

    /**
     * Find an Avatar by its file name.
     * 
     * @param fileName the file name of the avatar
     * @return an Optional containing the Avatar if found, or an empty Optional if
     *         not found
     */
    Optional<Avatar> findByFileName(String fileName);

    /**
     * Find all unique avatar file names, ordered by file name.
     * 
     * @return a list of all unique avatar file names, ordered alphabetically
     */
    @Query("SELECT DISTINCT t.fileName FROM Avatar t ORDER BY t.fileName")
    List<String> findAllAvatarFileNames();
}
