ALTER TABLE matching_profile
    ADD CONSTRAINT fk_matching_profile_student
        FOREIGN KEY (student_id)
            REFERENCES student (id)
            ON DELETE CASCADE;
