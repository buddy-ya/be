package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;

import java.time.LocalDateTime;

public record ChatroomResponse(
        Long roomId,
        String chatroomName,
        int unreadCount,
        String buddyProfileImage,
        String lastMessage,
        LocalDateTime lastMessageTime
) {

    public static ChatroomResponse from(Chatroom chatroom, ChatroomStudent chatroomStudent, String buddyProfileImage) {
        return new ChatroomResponse(chatroom.getId(), chatroom.getName(), chatroomStudent.getUnreadCount(), buddyProfileImage, chatroom.getLastMessage(), chatroom.getLastMessageTime());
    }
}
