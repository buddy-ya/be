package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;

import java.time.LocalDateTime;

public record ChatroomResponse(
        Long id,
        String name,
        String country,
        int unreadCount,
        String profileImageUrl,
        String lastMessage,
        LocalDateTime lastMessageDate,
        Long buddyId,
        boolean isBuddyExited
) {

    public static ChatroomResponse from(Chatroom chatroom, String name, String country, ChatroomStudent chatroomStudent, String buddyProfileImage, Long buddyId, boolean isBuddyLeave) {
        return new ChatroomResponse(chatroom.getId(), name, country, chatroomStudent.getUnreadCount(), buddyProfileImage, chatroom.getLastMessage(), chatroom.getLastMessageTime(), buddyId, isBuddyLeave);
    }
}
