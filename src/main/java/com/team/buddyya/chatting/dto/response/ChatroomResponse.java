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
        LocalDateTime lastMessageDate,
        boolean isBuddyLeft
) {

    public static ChatroomResponse from(Chatroom chatroom, String name, ChatroomStudent chatroomStudent, String buddyProfileImage, boolean isBuddyLeft) {
        return new ChatroomResponse(chatroom.getId(), name, chatroomStudent.getUnreadCount(), buddyProfileImage, chatroom.getLastMessage(), chatroom.getLastMessageTime(), isBuddyLeft);
    }
}
