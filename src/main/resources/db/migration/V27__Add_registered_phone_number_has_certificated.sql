ALTER TABLE registered_phone_number
    ADD has_certificated BIT(1) NULL;

UPDATE registered_phone_number rpn
    JOIN student s ON rpn.phone_number = s.phone_number
    SET rpn.has_certificated = s.certificated;

UPDATE registered_phone_number
SET has_certificated = false
WHERE has_certificated IS NULL;

ALTER TABLE registered_phone_number
    MODIFY has_certificated BIT(1) NOT NULL;
