package com.team.buddyya.feed.domain;

public record CommentInfo(
        Comment comment,
        boolean isFeedOwner,
        boolean isCommentOwner
) {

    public static CommentInfo from(Comment comment, Long feedOwnerId, Long currentUserId) {
        return new CommentInfo(
                comment,
                feedOwnerId.equals(comment.getStudent().getId()),
                currentUserId.equals(comment.getStudent().getId())
        );
    }
}
