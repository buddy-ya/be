package com.team.buddyya.feed.domain;

public record FeedUserAction(
        boolean isFeedOwner,
        boolean isLiked,
        boolean isBookmarked
) {

    public static FeedUserAction of(boolean isFeedOwner, boolean isLiked, boolean isBookmarked) {
        return new FeedUserAction(isFeedOwner, isLiked, isBookmarked);
    }
}
