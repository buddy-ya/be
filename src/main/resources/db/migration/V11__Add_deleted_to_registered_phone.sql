ALTER TABLE registered_phone_number
    ADD COLUMN deleted BIT(1) NULL;

UPDATE registered_phone_number
SET deleted = 0
WHERE deleted IS NULL;

ALTER TABLE registered_phone_number
    MODIFY COLUMN deleted BIT(1) NOT NULL;