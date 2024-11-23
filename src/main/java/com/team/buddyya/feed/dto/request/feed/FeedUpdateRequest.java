package com.team.buddyya.feed.dto.request.feed;

public record FeedUpdateRequest(
        String title,
        String content,
        String category
) {

    public static FeedUpdateRequest from(String title, String content, String category) {
        return new FeedUpdateRequest(title, content, category);
    }
}
