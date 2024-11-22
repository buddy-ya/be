package com.team.buddyya.feed.dto.response;

public record FeedCreateResponse(Long id) {

    public static FeedCreateResponse from(Long id) {
        return new FeedCreateResponse(id);
    }
}
