CREATE INDEX idx_feed_pagination ON feed (university_id, category_id, pinned DESC, created_date DESC, id DESC);
