CREATE TABLE match_request
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    created_date datetime              NOT NULL,
    updated_date datetime              NOT NULL,
    student_id   BIGINT                NOT NULL,
    korean       BIT(1)                NOT NULL,
    CONSTRAINT pk_match_request PRIMARY KEY (id)
);

CREATE TABLE matched_history
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    student_id BIGINT                NOT NULL,
    buddy_id   BIGINT                NOT NULL,
    CONSTRAINT pk_matched_history PRIMARY KEY (id)
);

ALTER TABLE matched_history
    ADD CONSTRAINT FK_MATCHED_HISTORY_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE match_request
    ADD CONSTRAINT FK_MATCH_REQUEST_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE student
    MODIFY is_deleted BIT(1) NOT NULL;