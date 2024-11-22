package com.team.buddyya.feed.dto.request;

public record FeedUpdateRequest(
        String title,
        String content,
        String category
) {
}
