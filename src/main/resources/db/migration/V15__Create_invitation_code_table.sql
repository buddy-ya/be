CREATE TABLE invitation_code
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    code                VARCHAR(255) NOT NULL,
    participated        BIT(1)       NOT NULL,
    student_id          BIGINT       NOT NULL,
    registered_phone_id BIGINT       NOT NULL,
    CONSTRAINT pk_invitation_code PRIMARY KEY (id)
);

ALTER TABLE invitation_code
    ADD CONSTRAINT uc_invitation_code_code UNIQUE (code);

ALTER TABLE invitation_code
    ADD CONSTRAINT uc_invitation_code_student UNIQUE (student_id);

ALTER TABLE invitation_code
    ADD CONSTRAINT FK_INVITATION_CODE_ON_REGISTERED_PHONE FOREIGN KEY (registered_phone_id) REFERENCES registered_phone_number (id);

ALTER TABLE invitation_code
    ADD CONSTRAINT FK_INVITATION_CODE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE registered_phone_number DROP INDEX authentication_code;