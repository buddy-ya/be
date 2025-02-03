package com.team.buddyya.chatting.dto.response;

public record ChatroomDetailResponse(
        Long id,
        String name,
        String country,
        String profileImageUrl,
        Boolean isBuddyExited
) {
    public static ChatroomDetailResponse from(Long roomId, String name, String country, String profileImageUrl, Boolean isBuddyExited) {
        return new ChatroomDetailResponse(roomId, name, country, profileImageUrl, isBuddyExited);
    }
}
