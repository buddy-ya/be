ALTER TABLE university
    ADD COLUMN match_active BIT(1) NOT NULL DEFAULT 1,
    ADD COLUMN feed_active  BIT(1) NOT NULL DEFAULT 0;
