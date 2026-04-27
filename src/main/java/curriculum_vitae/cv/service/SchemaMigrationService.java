package curriculum_vitae.cv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SchemaMigrationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SchemaMigrationService.class);
    private final JdbcTemplate jdbcTemplate;

    public SchemaMigrationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        ensureProfileDescriptionIsText();
    }

    private void ensureProfileDescriptionIsText() {
        try {
            Integer exists = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'curriculums'
                  AND column_name = 'profile_description'
                """,
                Integer.class
            );

            if (exists == null || exists == 0) {
                return;
            }

            String dataType = jdbcTemplate.queryForObject(
                """
                SELECT DATA_TYPE
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'curriculums'
                  AND column_name = 'profile_description'
                """,
                String.class
            );

            if (dataType == null) {
                return;
            }

            String normalized = dataType.trim().toLowerCase();
            if ("text".equals(normalized) || "mediumtext".equals(normalized) || "longtext".equals(normalized)) {
                return;
            }

            jdbcTemplate.execute("ALTER TABLE curriculums MODIFY COLUMN profile_description TEXT NOT NULL");
            logger.info("Schema fix applied: curriculums.profile_description converted to TEXT");
        } catch (Exception ex) {
            logger.warn("Schema fix skipped/failed for curriculums.profile_description: {}", ex.getMessage());
        }
    }
}