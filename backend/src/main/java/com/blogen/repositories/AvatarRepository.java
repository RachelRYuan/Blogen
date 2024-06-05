package com.blogen.repositories;

import com.blogen.domain.Avatar;

import java.util.List;
import java.util.Optional;

/**
 * Custom repository interface for Avatar entity.
 * This interface defines custom methods for interacting with Avatar data.
 * 
 * Author: Cliff
 * Refine: Rachel
 */
public interface AvatarRepository {

    /**
     * Save an Avatar entity.
     * 
     * @param avatar the Avatar entity to save
     * @return the saved Avatar entity
     */
    Avatar save(Avatar avatar);

    /**
     * Find an Avatar by its ID.
     * 
     * @param id the ID of the Avatar
     * @return an Optional containing the Avatar if found, or an empty Optional if
     *         not found
     */
    Optional<Avatar> findById(Long id);

    /**
     * Find an Avatar by its file name.
     * 
     * @param fileName the file name of the Avatar
     * @return an Optional containing the Avatar if found, or an empty Optional if
     *         not found
     */
    Optional<Avatar> findByFileName(String fileName);

    /**
     * Find all unique avatar file names, ordered by file name.
     * 
     * @return a list of all unique avatar file names, ordered alphabetically
     */
    List<String> findAllAvatarFileNames();
}
