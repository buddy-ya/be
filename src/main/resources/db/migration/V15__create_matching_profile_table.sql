CREATE TABLE matching_profile
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id     BIGINT  NOT NULL UNIQUE,
    introduction   TEXT,
    buddy_activity TEXT,
    completed      BOOLEAN NOT NULL DEFAULT FALSE
);
