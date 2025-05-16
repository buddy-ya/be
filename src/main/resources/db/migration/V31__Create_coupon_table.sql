CREATE TABLE coupon
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    code VARCHAR(10) NOT NULL,
    used BIT(1)      NOT NULL,
    CONSTRAINT pk_coupon PRIMARY KEY (id)
);

ALTER TABLE coupon
    ADD CONSTRAINT uc_coupon_code UNIQUE (code);