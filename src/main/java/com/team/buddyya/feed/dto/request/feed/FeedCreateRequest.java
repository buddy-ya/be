package com.team.buddyya.feed.dto.request.feed;

public record FeedCreateRequest(
        String title,
        String content,
        String category
) {
}
