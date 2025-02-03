package com.team.buddyya.feed.dto.response.comment;

import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.student.domain.UserProfileDefaultImage;
import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long id,
        Long userId,
        String content,
        String name,
        String country,
        String university,
        String profileImageUrl,
        int likeCount,
        LocalDateTime createdDate,
        boolean isDeleted,
        boolean isFeedOwner,
        boolean isCommentOwner,
        boolean isProfileImageUpload,
        List<CommentResponse> replies
) {

    public static CommentResponse from(Comment comment, Long feedOwnerId, Long currentUserId) {
        boolean isFeedOwner = feedOwnerId.equals(comment.getStudent().getId());
        boolean isCommentOwner = currentUserId.equals(comment.getStudent().getId());
        boolean isProfileImageUpload = UserProfileDefaultImage.isProfileImageUpload(comment.getStudent());
        List<CommentResponse> replies = comment.getChildren().stream()
                .map(reply -> CommentResponse.from(reply, feedOwnerId, currentUserId))
                .toList();
        return new CommentResponse(
                comment.getId(),
                comment.getStudent().getId(),
                comment.getContent(),
                comment.getStudent().getName(),
                comment.getStudent().getCountry(),
                comment.getStudent().getUniversity().getUniversityName(),
                comment.getStudent().getCharacterProfileImage(),
                comment.getLikeCount(),
                comment.getCreatedDate(),
                comment.isDeleted(),
                isFeedOwner,
                isCommentOwner,
                isProfileImageUpload,
                replies
        );
    }
}
