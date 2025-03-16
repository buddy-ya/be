package com.team.buddyya.admin.dto.response;

import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.MessageType;

import java.time.LocalDateTime;

public record AdminChatMessageResponse(
        Long id,
        Long senderId,
        MessageType type,
        String message,
        LocalDateTime createdDate
) {
    public static AdminChatMessageResponse from(Chat chat) {
        return new AdminChatMessageResponse(
                chat.getId(),
                chat.getStudent().getId(),
                chat.getType(),
                chat.getMessage(),
                chat.getCreatedDate()
        );
    }
}
