package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;

import java.time.LocalDateTime;

public record ChatroomsResponse(
        Long id,
        String name,
        int unreadCount,
        String profileImageUrl,
        String lastMessage,
        LocalDateTime lastMessageDate,
        boolean isBuddyExited
) {

    public static ChatroomsResponse from(Chatroom chatroom, String name, ChatroomStudent chatroomStudent, String buddyProfileImage, boolean isBuddyLeave) {
        return new ChatroomsResponse(chatroom.getId(), name, chatroomStudent.getUnreadCount(), buddyProfileImage, chatroom.getLastMessage(), chatroom.getLastMessageTime(), isBuddyLeave);
    }
}
