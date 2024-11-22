package com.team.buddyya.feed.dto.response.comment;

import com.team.buddyya.feed.domain.Comment;
import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String name,
        String country,
        LocalDateTime createdDate,
        boolean isFeedOwner,
        boolean isCommentOwner
) {

    public static CommentResponse from(Comment comment, boolean isFeedOwner, boolean isCommentOwner) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getStudent().getName(),
                comment.getStudent().getCountry(),
                comment.getCreatedDate(),
                isFeedOwner,
                isCommentOwner
        );
    }
}
