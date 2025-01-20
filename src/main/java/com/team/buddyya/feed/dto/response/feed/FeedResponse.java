package com.team.buddyya.feed.dto.response.feed;

import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedImage;
import com.team.buddyya.feed.domain.FeedUserAction;
import java.time.LocalDateTime;
import java.util.List;

public record FeedResponse(
        Long id,
        Long userId,
        String name,
        String country,
        String title,
        String content,
        String university,
        String profileImageUrl,
        List<String> imageUrls,
        int likeCount,
        int commentCount,
        boolean isFeedOwner,
        boolean isLiked,
        boolean isBookmarked,
        LocalDateTime createdDate
) {

    public static FeedResponse from(Feed feed, FeedUserAction userAction) {
        return new FeedResponse(
                feed.getId(),
                feed.getStudent().getId(),
                feed.getStudent().getName(),
                feed.getStudent().getCountry(),
                feed.getTitle(),
                feed.getContent(),
                feed.getStudent().getUniversity().getUniversityName(),
                feed.getStudent().getProfileImage().getUrl(),
                feed.getImages().stream()
                        .map(FeedImage::getUrl)
                        .toList(),
                feed.getLikeCount(),
                feed.getCommentCount(),
                userAction.isFeedOwner(),
                userAction.isLiked(),
                userAction.isBookmarked(),
                feed.getCreatedDate()
        );
    }
}
