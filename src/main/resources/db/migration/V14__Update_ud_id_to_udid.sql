ALTER TABLE phone_info
    ADD udid VARCHAR(255) NULL;

ALTER TABLE phone_info
    ADD CONSTRAINT uc_phone_info_udid UNIQUE (udid);

ALTER TABLE phone_info
DROP
COLUMN ud_id;