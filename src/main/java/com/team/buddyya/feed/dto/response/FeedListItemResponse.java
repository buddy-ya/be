package com.team.buddyya.feed.dto.response;

import com.team.buddyya.feed.domain.Feed;
import java.time.LocalDateTime;

public record FeedListItemResponse(
        Long id,
        String name,
        String country,
        String title,
        String content,
        int likeCount,
        int commentCount,
        boolean isLiked,
        boolean isBookmarked,
        LocalDateTime createdDate
) {

    public static FeedListItemResponse from(Feed feed, boolean isLiked, boolean isBookmarked) {
        return new FeedListItemResponse(
                feed.getId(),
                feed.getStudent().getName(),
                feed.getStudent().getCountry(),
                feed.getTitle(),
                feed.getContent(),
                feed.getLikeCount(),
                feed.getCommentCount(),
                isLiked,
                isBookmarked,
                feed.getCreatedDate()
        );
    }
}
