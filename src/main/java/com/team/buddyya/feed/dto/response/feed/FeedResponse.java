package com.team.buddyya.feed.dto.response.feed;

import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedImage;
import com.team.buddyya.feed.domain.FeedUserAction;
import com.team.buddyya.student.domain.UserProfileDefaultImage;
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
        int viewCount,
        boolean isFeedOwner,
        boolean isLiked,
        boolean isBookmarked,
        boolean isProfileImageUpload,
        LocalDateTime createdDate
) {

    public static FeedResponse from(Feed feed, FeedUserAction userAction) {
        boolean isProfileImageUpload = UserProfileDefaultImage.isProfileImageUpload(feed.getStudent());

        return new FeedResponse(
                feed.getId(),
                feed.getStudent().getId(),
                feed.getStudent().getName(),
                feed.getStudent().getCountry(),
                feed.getTitle(),
                feed.getContent(),
                feed.getStudent().getUniversity().getUniversityName(),
                feed.getStudent().getCharacterProfileImage(),
                feed.getImages().stream()
                        .map(FeedImage::getUrl)
                        .toList(),
                feed.getLikeCount(),
                feed.getCommentCount(),
                feed.getViewCount(),
                userAction.isFeedOwner(),
                userAction.isLiked(),
                userAction.isBookmarked(),
                isProfileImageUpload,
                feed.getCreatedDate()
        );
    }
}
