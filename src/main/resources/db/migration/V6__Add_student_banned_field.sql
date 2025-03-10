ALTER TABLE student
    ADD ban_end_time datetime NULL;

ALTER TABLE student
    ADD banned BIT(1) NOT NULL DEFAULT b'0';

ALTER TABLE student_id_card
    ADD rejection_reason VARCHAR(255) NULL;

ALTER TABLE student
    CHANGE COLUMN is_deleted deleted BIT(1) NOT NULL DEFAULT b'0';

