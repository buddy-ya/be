package com.team.buddyya.feed.dto.response.comment;

import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.repository.CommentLikeRepository;
import com.team.buddyya.student.domain.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
        boolean isLiked,
        boolean isFeedOwner,
        boolean isCommentOwner,
        boolean isBlocked,
        boolean isCertificated,
        boolean isStudentDeleted,
        List<CommentResponse> replies
) {

    private static final String BUDDYYA_PROFILE_IMAGE =
            "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/buddyya_icon.png";

    public static CommentResponse from(Comment comment, Long feedOwnerId, Long currentUserId,
                                       CommentLikeRepository commentLikeRepository, Set<Long> blockedStudentIds) {
        String profileImageUrl = comment.getStudent().getRole() == Role.OWNER ? BUDDYYA_PROFILE_IMAGE
                : comment.getStudent().getCharacterProfileImage();
        boolean isFeedOwner = feedOwnerId.equals(comment.getStudent().getId());
        boolean isCommentOwner = currentUserId.equals(comment.getStudent().getId());
        boolean isLiked = commentLikeRepository.existsByCommentAndStudentId(comment, currentUserId);
        boolean isBlocked = blockedStudentIds.contains(comment.getStudent().getId());
        List<CommentResponse> replies = comment.getChildren().stream()
                .map(reply -> CommentResponse.from(reply, feedOwnerId, currentUserId, commentLikeRepository,
                        blockedStudentIds))
                .toList();
        return new CommentResponse(
                comment.getId(),
                comment.getStudent().getId(),
                comment.getContent(),
                comment.getStudent().getName(),
                comment.getStudent().getCountry(),
                comment.getStudent().getUniversity().getUniversityName(),
                profileImageUrl,
                comment.getLikeCount(),
                comment.getCreatedDate(),
                comment.isDeleted(),
                isLiked,
                isFeedOwner,
                isCommentOwner,
                isBlocked,
                comment.getStudent().getIsCertificated(),
                comment.getStudent().getIsDeleted(),
                replies
        );
    }
}
