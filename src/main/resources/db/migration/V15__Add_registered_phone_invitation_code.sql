ALTER TABLE registered_phone_number
    ADD invitation_code VARCHAR(255) NULL;

ALTER TABLE registered_phone_number
    ADD invitation_event_participated BIT(1) NULL;

ALTER TABLE registered_phone_number
    MODIFY invitation_code VARCHAR (255) NOT NULL;

ALTER TABLE registered_phone_number
    MODIFY invitation_event_participated BIT (1) NOT NULL;

ALTER TABLE registered_phone_number
    ADD CONSTRAINT uc_registered_phone_number_invitation_code UNIQUE (invitation_code);

ALTER TABLE registered_phone_number DROP INDEX authentication_code;