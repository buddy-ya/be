package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;

import java.time.LocalDateTime;

public record ChatroomResponse(
        Long id,
        String name,
        int unreadCount,
        String profileImageUrl,
        String lastMessage,
        LocalDateTime lastMessageDate
) {

    public static ChatroomResponse from(Chatroom chatroom, ChatroomStudent chatroomStudent, String buddyProfileImage) {
        return new ChatroomResponse(chatroom.getId(), chatroom.getName(), chatroomStudent.getUnreadCount(), buddyProfileImage, chatroom.getLastMessage(), chatroom.getLastMessageTime());
    }
}
