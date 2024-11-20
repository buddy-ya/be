package com.team.buddyya.feed.dto.response;

import java.time.LocalDateTime;

public record FeedListItemResponse(
        Long id,
        String studentName,
        String country,
        String title,
        String content,
        int likeCount,
        int commentCount,
        boolean isLiked,
        boolean isBookmarked,
        LocalDateTime createdDate
) {
}
