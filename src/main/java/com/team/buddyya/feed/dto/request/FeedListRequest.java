package com.team.buddyya.feed.dto.request;

public record FeedListRequest(
        int page,
        int size,
        String category,
        String keyword
) {
}
