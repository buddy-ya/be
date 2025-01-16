package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        Long chatroomId,
        String chatroomName,
        int unreadCount,
        String buddyProfileImage,
        String lastMessage,
        LocalDateTime lastMessageTime,
        Long postId
) {

    public static ChatRoomResponse from(Chatroom chatroom, ChatroomStudent chatroomStudent, String buddyProfileImage) {
        return new ChatRoomResponse(chatroom.getId(), chatroom.getName(), chatroomStudent.getUnreadCount(), buddyProfileImage, chatroom.getLastMessage(), chatroom.getLastMessageTime(), chatroom.getPostId());
    }
}
