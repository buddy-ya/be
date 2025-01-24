package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.MessageType;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long senderId,
        MessageType type,
        String message,
        LocalDateTime createdDate
) {

    public static ChatMessageResponse from(Chat chat) {
        return new ChatMessageResponse(
                chat.getId(),
                chat.getStudent().getId(),
                chat.getType(),
                chat.getMessage(),
                chat.getCreatedDate()
        );
    }
}
