-- MySQL 8.4. Run with the backend stopped, after backing up the database.
-- The existing career_roles table contains sections. Keeping it and its IDs
-- preserves career_activities.career_role_id without copying activity rows.
-- Safe to rerun after a partial DDL failure; no existing data is deleted.

DELIMITER $$
DROP PROCEDURE IF EXISTS migrate_career_positions$$
CREATE PROCEDURE migrate_career_positions()
BEGIN
    DECLARE source_tables INT DEFAULT 0;
    DECLARE expected_columns INT DEFAULT 0;
    DECLARE missing_parents BIGINT DEFAULT 0;
    DECLARE missing_positions BIGINT DEFAULT 0;
    DECLARE mismatched_sections BIGINT DEFAULT 0;
    DECLARE unmatched_activities BIGINT DEFAULT 0;
    DECLARE has_position_column INT DEFAULT 0;
    DECLARE column_nullable VARCHAR(3);
    DECLARE has_foreign_key INT DEFAULT 0;

    SELECT COUNT(*) INTO source_tables
    FROM information_schema.tables
    WHERE table_schema = DATABASE() AND table_name IN ('careers', 'career_roles', 'career_activities');
    IF source_tables <> 3 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Expected careers, career_roles and career_activities; inspect the production schema first';
    END IF;

    SELECT COUNT(*) INTO expected_columns
    FROM information_schema.columns c
    WHERE c.table_schema = DATABASE()
      AND ((c.table_name = 'careers' AND c.column_name = 'id' AND c.data_type = 'bigint' AND c.column_type NOT LIKE '%unsigned%')
        OR (c.table_name = 'career_roles' AND c.column_name IN ('id', 'career_id') AND c.data_type = 'bigint' AND c.column_type NOT LIKE '%unsigned%')
        OR (c.table_name = 'career_activities' AND c.column_name = 'career_role_id' AND c.data_type = 'bigint' AND c.column_type NOT LIKE '%unsigned%')
        OR (c.table_name = 'careers' AND c.column_name IN ('start_date', 'end_date') AND c.data_type = 'date'));
    IF expected_columns <> 6 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Career ID or date columns differ from the expected Hibernate schema';
    END IF;

    SELECT COUNT(*) INTO missing_parents
    FROM career_roles s LEFT JOIN careers c ON c.id = s.career_id
    WHERE s.career_id IS NOT NULL AND c.id IS NULL;
    IF missing_parents <> 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Orphaned career_roles rows found; inspect before migrating';
    END IF;

    SELECT COUNT(*) INTO unmatched_activities
    FROM career_activities a LEFT JOIN career_roles s ON s.id = a.career_role_id
    WHERE s.id IS NULL;
    IF unmatched_activities <> 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Orphaned career_activities rows found; inspect before migrating';
    END IF;

    CREATE TABLE IF NOT EXISTS career_positions (
        id BIGINT NOT NULL AUTO_INCREMENT,
        career_id BIGINT NOT NULL,
        title VARCHAR(100) NULL,
        start_date DATE NOT NULL,
        end_date DATE NULL,
        display_order INT NOT NULL,
        PRIMARY KEY (id),
        KEY idx_career_positions_career_order (career_id, display_order),
        CONSTRAINT fk_career_positions_career FOREIGN KEY (career_id) REFERENCES careers (id)
    ) ENGINE=InnoDB;

    SELECT COUNT(*) INTO has_position_column
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'career_roles' AND column_name = 'position_id';
    IF has_position_column = 0 THEN
        ALTER TABLE career_roles ADD COLUMN position_id BIGINT NULL;
    END IF;

    START TRANSACTION;
    INSERT INTO career_positions (career_id, title, start_date, end_date, display_order)
    SELECT c.id, NULL, c.start_date, c.end_date, 1
    FROM careers c
    WHERE NOT EXISTS (SELECT 1 FROM career_positions p WHERE p.career_id = c.id);

    UPDATE career_roles s
    JOIN career_positions p ON p.career_id = s.career_id AND p.display_order = 1
    SET s.position_id = p.id
    WHERE s.position_id IS NULL;

    SELECT COUNT(*) INTO missing_positions FROM career_roles WHERE position_id IS NULL;
    SELECT COUNT(*) INTO mismatched_sections
    FROM career_roles s JOIN career_positions p ON p.id = s.position_id
    WHERE s.career_id IS NOT NULL AND s.career_id <> p.career_id;
    IF missing_positions <> 0 OR mismatched_sections <> 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Section-to-position ownership check failed';
    END IF;
    COMMIT;

    SELECT c.is_nullable INTO column_nullable
    FROM information_schema.columns c
    WHERE c.table_schema = DATABASE() AND c.table_name = 'career_roles' AND c.column_name = 'position_id';
    IF column_nullable = 'YES' THEN
        ALTER TABLE career_roles MODIFY COLUMN position_id BIGINT NOT NULL;
    END IF;

    SELECT COUNT(*) INTO has_foreign_key
    FROM information_schema.key_column_usage
    WHERE table_schema = DATABASE() AND table_name = 'career_roles'
      AND column_name = 'position_id' AND referenced_table_name = 'career_positions';
    IF has_foreign_key = 0 THEN
        ALTER TABLE career_roles ADD INDEX idx_career_roles_position_order (position_id, display_order),
            ADD CONSTRAINT fk_career_roles_position FOREIGN KEY (position_id) REFERENCES career_positions (id);
    END IF;

    -- Legacy rows retain career_id. New sections use position_id only.
    SELECT c.is_nullable INTO column_nullable
    FROM information_schema.columns c
    WHERE c.table_schema = DATABASE() AND c.table_name = 'career_roles' AND c.column_name = 'career_id';
    IF column_nullable = 'NO' THEN
        ALTER TABLE career_roles MODIFY COLUMN career_id BIGINT NULL;
    END IF;
END$$
DELIMITER ;

CALL migrate_career_positions();
DROP PROCEDURE migrate_career_positions;

-- Verify counts and ownership against the pre-migration backup.
SELECT (SELECT COUNT(*) FROM careers) AS careers,
       (SELECT COUNT(*) FROM career_positions) AS positions,
       (SELECT COUNT(*) FROM career_roles) AS sections,
       (SELECT COUNT(*) FROM career_activities) AS activities;
SELECT COUNT(*) AS unlinked_sections FROM career_roles WHERE position_id IS NULL;
SELECT COUNT(*) AS mismatched_legacy_sections
FROM career_roles s JOIN career_positions p ON p.id = s.position_id
WHERE s.career_id IS NOT NULL AND s.career_id <> p.career_id;
