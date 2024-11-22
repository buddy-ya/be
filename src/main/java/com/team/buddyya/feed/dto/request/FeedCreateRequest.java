package com.team.buddyya.feed.dto.request;

public record FeedCreateRequest(
        String title,
        String content,
        String category
) {
}
