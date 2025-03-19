ALTER TABLE match_request
    ADD gender_type VARCHAR(255) NULL;

ALTER TABLE match_request
    ADD match_request_status VARCHAR(255) NULL;

ALTER TABLE match_request
    ADD university_id BIGINT NULL;

ALTER TABLE match_request
    ADD university_type VARCHAR(255) NULL;

ALTER TABLE match_request
    MODIFY gender_type VARCHAR (255) NOT NULL;

ALTER TABLE match_request
    MODIFY match_request_status VARCHAR (255) NOT NULL;

ALTER TABLE match_request
    MODIFY university_id BIGINT NOT NULL;

ALTER TABLE match_request
    MODIFY university_type VARCHAR (255) NOT NULL;