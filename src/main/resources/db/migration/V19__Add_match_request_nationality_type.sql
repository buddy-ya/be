ALTER TABLE match_request
    ADD nationality_type VARCHAR(255) NULL;

ALTER TABLE match_request
    MODIFY nationality_type VARCHAR (255) NOT NULL;

