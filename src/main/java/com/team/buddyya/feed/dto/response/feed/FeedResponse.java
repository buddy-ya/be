package com.team.buddyya.feed.dto.response.feed;

import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.domain.FeedImage;
import com.team.buddyya.feed.domain.FeedUserAction;
import com.team.buddyya.student.domain.Role;
import java.time.LocalDateTime;
import java.util.List;

public record FeedResponse(
        Long id,
        Long userId,
        String universityTab,
        String category,
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
        boolean isPinned,
        boolean isCertificated,
        boolean isProfileVisible,
        boolean isStudentDeleted,
        LocalDateTime createdDate
) {

    private static final String BUDDYYA_PROFILE_IMAGE =
            "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/buddyya_icon.png";

    public static FeedResponse from(Feed feed, FeedUserAction userAction) {
        String profileImageUrl = feed.getStudent().getRole() == Role.OWNER ? BUDDYYA_PROFILE_IMAGE
                : feed.getStudent().getCharacterProfileImage();
        return new FeedResponse(
                feed.getId(),
                feed.getStudent().getId(),
                feed.getUniversity().getUniversityName(),
                feed.getCategory().getName(),
                feed.getStudent().getName(),
                feed.getStudent().getCountry(),
                feed.getTitle(),
                feed.getContent(),
                feed.getStudent().getUniversity().getUniversityName(),
                profileImageUrl,
                feed.getImages().stream()
                        .map(FeedImage::getUrl)
                        .toList(),
                feed.getLikeCount(),
                feed.getCommentCount(),
                feed.getViewCount(),
                userAction.isFeedOwner(),
                userAction.isLiked(),
                userAction.isBookmarked(),
                feed.isPinned(),
                feed.getStudent().getIsCertificated(),
                feed.isProfileVisible(),
                feed.getStudent().getIsDeleted(),
                feed.getCreatedDate()
        );
    }
}
