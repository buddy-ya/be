package com.team.buddyya.feed.dto.response.comment;

import com.team.buddyya.feed.domain.CommentInfo;
import java.time.LocalDateTime;

public record CommentCreateResponse(
        Long id,
        String content,
        String name,
        String country,
        LocalDateTime createdDate,
        boolean isFeedOwner,
        boolean isCommentOwner
) {

    public static CommentCreateResponse from(CommentInfo info) {
        return new CommentCreateResponse(
                info.comment().getId(),
                info.comment().getContent(),
                info.comment().getStudent().getName(),
                info.comment().getStudent().getCountry(),
                info.comment().getCreatedDate(),
                info.isFeedOwner(),
                info.isCommentOwner()
        );
    }
}
