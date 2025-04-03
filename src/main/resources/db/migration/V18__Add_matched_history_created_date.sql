ALTER TABLE matched_history
    ADD created_date DATETIME NULL;

UPDATE matched_history
SET created_date = NOW()
WHERE created_date IS NULL;

ALTER TABLE matched_history
    MODIFY created_date DATETIME NOT NULL;