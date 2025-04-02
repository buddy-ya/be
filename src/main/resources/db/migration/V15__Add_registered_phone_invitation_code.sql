ALTER TABLE registered_phone_number
    ADD invitation_code VARCHAR(6) NULL;

ALTER TABLE registered_phone_number
    ADD invitation_event_participated BIT(1) NULL;

UPDATE registered_phone_number
SET invitation_event_participated = 0
WHERE invitation_event_participated IS NULL;

ALTER TABLE registered_phone_number
    MODIFY invitation_event_participated BIT (1) NOT NULL;

ALTER TABLE registered_phone_number
    ADD CONSTRAINT uc_registered_phone_number_invitation_code UNIQUE (invitation_code);

ALTER TABLE registered_phone_number DROP INDEX authentication_code;
