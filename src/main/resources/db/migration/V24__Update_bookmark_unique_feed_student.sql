ALTER TABLE bookmark
    ADD CONSTRAINT uq_bookmark_feed_student UNIQUE (feed_id, student_id);
