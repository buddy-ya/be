ALTER TABLE feed_like
    ADD CONSTRAINT uq_feed_like_feed_student UNIQUE (feed_id, student_id);
