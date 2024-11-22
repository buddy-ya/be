package com.team.buddyya.feed.dto.response;

import java.util.List;

public record CommentListResponse(List<CommentResponse> comments) {

    public static CommentListResponse from(List<CommentResponse> comments) {
        return new CommentListResponse(comments);
    }
}
