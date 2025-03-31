ALTER TABLE match_request
    ADD COLUMN chatroom_id BIGINT NULL;

UPDATE match_request
SET chatroom_id = NULL
WHERE chatroom_id IS NOT NULL;