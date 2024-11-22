package com.team.buddyya.feed.dto.response.comment;

import com.team.buddyya.feed.domain.Comment;
import java.time.LocalDateTime;

public record CommentCreateResponse(
        Long id,
        String content,
        String name,
        String country,
        LocalDateTime createdDate
) {

    public static CommentCreateResponse from(Comment comment) {
        return new CommentCreateResponse(
                comment.getId(),
                comment.getContent(),
                comment.getStudent().getName(),
                comment.getStudent().getCountry(),
                comment.getCreatedDate()
        );
    }
}
