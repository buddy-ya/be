ALTER TABLE match_request
    ADD nationality_type VARCHAR(255) NULL;

ALTER TABLE match_request
    MODIFY nationality_type VARCHAR (255) NOT NULL;

ALTER TABLE matching_profile
    ADD CONSTRAINT FK_MATCHING_PROFILE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE matching_profile
    MODIFY completed BIT (1) NULL;

ALTER TABLE student
DROP
COLUMN gender;

ALTER TABLE student
DROP
COLUMN `role`;

ALTER TABLE student
    ADD gender VARCHAR(255) NOT NULL;

ALTER TABLE registered_phone_number
    MODIFY invitation_code VARCHAR (6) NOT NULL;

ALTER TABLE admin_account
    MODIFY phone_number VARCHAR (255);

ALTER TABLE test_account
    MODIFY phone_number VARCHAR (255);

ALTER TABLE student
    ADD `role` VARCHAR(255) NOT NULL;

ALTER TABLE matching_profile
    MODIFY student_id BIGINT NULL;

ALTER TABLE expo_token
    MODIFY token VARCHAR (255) NULL;

ALTER TABLE chat
DROP
COLUMN type;

ALTER TABLE chat
    ADD type VARCHAR(255) NOT NULL;

ALTER TABLE chatroom
    MODIFY type VARCHAR (255);

ALTER TABLE report
DROP
COLUMN type;

ALTER TABLE report
    ADD type VARCHAR(255) NOT NULL;