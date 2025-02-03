package com.team.buddyya.chatting.dto.response;

public record ChatroomDetailResponse(
        Long id,
        String name,
        String profileImageUrl,
        Boolean isKorean,
        Boolean isBuddyExited
) {
    public static ChatroomDetailResponse from(Long roomId, String name, String profileImageUrl, Boolean isKorean, Boolean isBuddyExited) {
        return new ChatroomDetailResponse(roomId, name, profileImageUrl, isKorean, isBuddyExited);
    }
}
