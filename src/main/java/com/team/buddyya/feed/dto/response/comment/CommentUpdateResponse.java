package com.team.buddyya.feed.dto.response.comment;

import com.team.buddyya.feed.domain.CommentInfo;
import java.time.LocalDateTime;


public record CommentUpdateResponse(
        Long id,
        String content,
        String name,
        String country,
        String university,
        String profileImageUrl,
        LocalDateTime updateTime,
        boolean isFeedOwner,
        boolean isCommentOwner
) {

    public static CommentUpdateResponse from(CommentInfo info) {
        return new CommentUpdateResponse(
                info.comment().getId(),
                info.comment().getContent(),
                info.comment().getStudent().getName(),
                info.comment().getStudent().getCountry(),
                info.comment().getStudent().getUniversity().getUniversityName(),
                info.comment().getStudent().getProfileImage().getUrl(),
                info.comment().getUpdatedDate(),
                info.isFeedOwner(),
                info.isCommentOwner()
        );
    }
}

