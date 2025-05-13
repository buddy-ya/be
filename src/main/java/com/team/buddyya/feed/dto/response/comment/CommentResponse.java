package com.team.buddyya.feed.dto.response.comment;

import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.repository.CommentLikeRepository;
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

    public static CommentResponse from(Comment comment, Long feedOwnerId, Long currentUserId,
                                       CommentLikeRepository commentLikeRepository, Set<Long> blockedStudentIds) {
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
                comment.getStudent().getCharacterProfileImage(),
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
