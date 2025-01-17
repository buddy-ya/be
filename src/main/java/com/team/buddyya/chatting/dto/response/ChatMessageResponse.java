package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chat;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long senderId,
        String message,
        Long messageId,
        LocalDateTime createdDate
) {

    public static ChatMessageResponse from(Chat chat) {
        return new ChatMessageResponse(
                chat.getStudent().getId(),
                chat.getMessage(),
                chat.getId(),
                chat.getCreatedDate()
        );
    }
}
