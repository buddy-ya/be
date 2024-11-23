package com.team.buddyya.feed.dto.request.feed;

public record FeedCreateRequest(
        String title,
        String content,
        String category
) {

    public static FeedCreateRequest from(String title, String content, String category) {
        return new FeedCreateRequest(title, content, category);
    }
}
