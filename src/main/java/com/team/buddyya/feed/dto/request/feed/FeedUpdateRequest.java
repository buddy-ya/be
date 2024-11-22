package com.team.buddyya.feed.dto.request.feed;

public record FeedUpdateRequest(
        String title,
        String content,
        String category
) {
}
