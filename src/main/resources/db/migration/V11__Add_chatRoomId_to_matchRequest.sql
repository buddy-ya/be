ALTER TABLE match_request
    ADD COLUMN chat_room_id BIGINT NULL;

UPDATE match_request
SET chat_room_id = NULL
WHERE chat_room_id IS NOT NULL;