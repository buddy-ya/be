package com.team.buddyya.feed.dto.request.feed;

public record FeedCursorListRequest(
        String university,
        String category,
        String keyword,
        Long lastId,
        Integer pageSize
) {
}
