CREATE TABLE point
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_date  datetime NOT NULL,
    updated_date  datetime NOT NULL,
    student_id    BIGINT   NOT NULL,
    current_point INT      NOT NULL,
    CONSTRAINT pk_point PRIMARY KEY (id)
);

CREATE TABLE point_status
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_date  datetime     NOT NULL,
    point_id      BIGINT       NOT NULL,
    point_type    VARCHAR(255) NOT NULL,
    changed_point INT          NOT NULL,
    CONSTRAINT pk_point_status PRIMARY KEY (id)
);

ALTER TABLE block
    ADD created_date datetime NULL;

ALTER TABLE block
    MODIFY created_date datetime NOT NULL;

ALTER TABLE point
    ADD CONSTRAINT uc_point_student UNIQUE (student_id);

ALTER TABLE point
    ADD CONSTRAINT FK_POINT_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE point_status
    ADD CONSTRAINT FK_POINT_STATUS_ON_POINT FOREIGN KEY (point_id) REFERENCES point (id);