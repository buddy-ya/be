ALTER TABLE match_request
    ADD chinese_available BIT(1) NULL;

ALTER TABLE match_request
    MODIFY chinese_available BIT (1) NOT NULL;

ALTER TABLE match_request
DROP
COLUMN korean;