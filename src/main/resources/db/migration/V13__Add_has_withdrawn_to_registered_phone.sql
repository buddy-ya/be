ALTER TABLE registered_phone_number
    ADD COLUMN has_withdrawn BIT(1) NULL;

UPDATE registered_phone_number
SET has_withdrawn = 0
WHERE has_withdrawn IS NULL;

ALTER TABLE registered_phone_number
    MODIFY COLUMN has_withdrawn BIT(1) NOT NULL;