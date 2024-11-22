package com.team.buddyya.feed.dto.response;

import com.team.buddyya.feed.domain.Comment;
import java.time.LocalDateTime;

public record CommentUpdateResponse(
        Long id,
        String content,
        String name,
        String country,
        LocalDateTime createdDate
) {
    
    public static CommentUpdateResponse from(Comment comment) {
        return new CommentUpdateResponse(
                comment.getId(),
                comment.getContent(),
                comment.getStudent().getName(),
                comment.getStudent().getCountry(),
                comment.getCreatedDate()
        );
    }
}
