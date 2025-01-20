package com.team.buddyya.feed.dto.response.comment;

import com.team.buddyya.feed.domain.CommentInfo;
import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long userId,
        String content,
        String name,
        String country,
        String university,
        String profileImageUrl,
        LocalDateTime createdDate,
        boolean isFeedOwner,
        boolean isCommentOwner
) {

    public static CommentResponse from(CommentInfo info) {
        return new CommentResponse(
                info.comment().getId(),
                info.comment().getStudent().getId(),
                info.comment().getContent(),
                info.comment().getStudent().getName(),
                info.comment().getStudent().getCountry(),
                info.comment().getStudent().getUniversity().getUniversityName(),
                info.comment().getStudent().getProfileImage().getUrl(),
                info.comment().getCreatedDate(),
                info.isFeedOwner(),
                info.isCommentOwner()
        );
    }
}
