package com.team.buddyya.feed.domain;

public record FeedUserAction(
        boolean isLiked,
        boolean isBookmarked
) {

    public static FeedUserAction from(boolean isLiked, boolean isBookmarked) {
        return new FeedUserAction(isLiked, isBookmarked);
    }
}
