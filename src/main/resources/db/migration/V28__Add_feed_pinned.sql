ALTER TABLE feed
    ADD COLUMN pinned BIT(1) NOT NULL DEFAULT 0;
