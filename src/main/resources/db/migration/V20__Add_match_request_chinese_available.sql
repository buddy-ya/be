ALTER TABLE match_request
    ADD chinese_available BIT(1) NULL;

ALTER TABLE match_request
    MODIFY chinese_available BIT (1) NOT NULL;

ALTER TABLE matching_profile
    ADD CONSTRAINT FK_MATCHING_PROFILE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE match_request
DROP
COLUMN korean;