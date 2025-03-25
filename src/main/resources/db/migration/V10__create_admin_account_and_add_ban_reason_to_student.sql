CREATE TABLE admin_account
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL UNIQUE
);

ALTER TABLE student
    ADD COLUMN ban_reason VARCHAR(255);