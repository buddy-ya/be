ALTER TABLE university
    ADD active BIT(1) NULL;

ALTER TABLE university
    MODIFY active BIT (1) NOT NULL;