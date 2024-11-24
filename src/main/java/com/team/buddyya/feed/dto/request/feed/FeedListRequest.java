package com.team.buddyya.feed.dto.request.feed;

public record FeedListRequest(
        String category,
        String keyword
) {

    public static FeedListRequest of(String category, String keyword) {
        return new FeedListRequest(category, keyword);
    }
}
