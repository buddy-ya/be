package com.team.buddyya.feed.dto.request.feed;

public record FeedListRequest(
        int page,
        int size,
        String category,
        String keyword
) {
}
