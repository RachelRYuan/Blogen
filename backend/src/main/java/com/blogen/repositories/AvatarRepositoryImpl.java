package com.blogen.repositories;

import com.blogen.domain.Avatar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the AvatarRepository using JdbcTemplate.
 * This implementation is not currently used in the project, but is kept for
 * reference purposes.
 * 
 * Author: Cliff
 * Refine: Rachel
 */
@Repository
@Slf4j
@Profile("jdbc")
public class AvatarRepositoryImpl implements AvatarRepository {

    private static final String FETCH_SQL_BY_ID = "SELECT id, file_name FROM avatar WHERE id = ?";
    private static final String FETCH_SQL_BY_FILENAME = "SELECT id, file_name FROM avatar WHERE file_name = ?";
    private static final String FETCH_ALL_FILENAMES = "SELECT DISTINCT file_name FROM avatar ORDER BY file_name";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AvatarRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Avatar save(Avatar avatar) {
        // Implementation to save Avatar would go here
        // For example, using jdbcTemplate.update() to insert/update the Avatar entity
        log.info("Saving Avatar: {}", avatar);
        return null; // Placeholder for actual save logic
    }

    @Override
    public Optional<Avatar> findById(Long id) {
        List<Avatar> avatars = jdbcTemplate.query(FETCH_SQL_BY_ID, new AvatarMapper(), id);
        return avatars.isEmpty() ? Optional.empty() : Optional.of(avatars.get(0));
    }

    @Override
    public Optional<Avatar> findByFileName(String fileName) {
        List<Avatar> avatars = jdbcTemplate.query(FETCH_SQL_BY_FILENAME, new AvatarMapper(), fileName);
        return avatars.isEmpty() ? Optional.empty() : Optional.of(avatars.get(0));
    }

    @Override
    public List<String> findAllAvatarFileNames() {
        return jdbcTemplate.query(FETCH_ALL_FILENAMES, (rs, rowNum) -> rs.getString("file_name"));
    }

    /**
     * RowMapper implementation to map a row of the Avatar table to an Avatar domain
     * object.
     */
    static class AvatarMapper implements RowMapper<Avatar> {

        @Override
        public Avatar mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Avatar.builder()
                    .id(rs.getLong("id"))
                    .fileName(rs.getString("file_name"))
                    .build();
        }
    }
}
