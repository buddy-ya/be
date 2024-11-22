package com.team.buddyya.feed.dto.response;

public record BookmarkResponse(boolean isBookmarked) {
    
    public static BookmarkResponse from(boolean isBookmarked) {
        return new BookmarkResponse(isBookmarked);
    }
}
