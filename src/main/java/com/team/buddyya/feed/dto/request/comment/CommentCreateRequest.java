package com.team.buddyya.feed.dto.request.comment;

public record CommentCreateRequest(Long parentId, String content) {
}
