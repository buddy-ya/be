CREATE TABLE phone_info
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    ud_id              VARCHAR(255) NOT NULL,
    send_message_count INT          NOT NULL,
    CONSTRAINT pk_phone_info PRIMARY KEY (id)
);

ALTER TABLE phone_info
    ADD CONSTRAINT uc_phone_info_ud UNIQUE (ud_id);