package com.team.buddyya.feed.dto.request.feed;

public record FeedListRequest(
        String university,
        String category,
        String keyword
) {

    public static FeedListRequest of(String category, String keyword) {
        return new FeedListRequest(university, category, keyword);
    }
}
