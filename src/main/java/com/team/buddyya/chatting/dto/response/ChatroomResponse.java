package com.team.buddyya.chatting.dto.response;

public record ChatroomResponse(
        Long id,
        String name,
        String profileImageUrl,
        boolean isBuddyExited
) {
    public static ChatroomResponse from(Long roomId, String name, String profileImageUrl, boolean isBuddyExited) {
        return new ChatroomResponse(roomId, name, profileImageUrl, isBuddyExited);
    }
}
