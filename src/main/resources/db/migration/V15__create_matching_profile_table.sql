CREATE TABLE matching_profile
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id     BIGINT  NOT NULL UNIQUE,
    introduction   TEXT,
    buddy_activity TEXT,
    completed      BOOLEAN NOT NULL DEFAULT FALSE
);

ALTER TABLE student_id_card
    ADD COLUMN updated_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
