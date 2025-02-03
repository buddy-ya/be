package com.team.buddyya.feed.dto.response;

public record LikeResponse(
        boolean isLiked,
        int likeCount
) {
    
    public static LikeResponse from(boolean isLiked, int likeCount) {
        return new LikeResponse(isLiked, likeCount);
    }
}
