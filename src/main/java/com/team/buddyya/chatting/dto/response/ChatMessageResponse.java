package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chat;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long senderId,
        String message,
        LocalDateTime createdDate
) {

    public static ChatMessageResponse from(Chat chat) {
        return new ChatMessageResponse(
                chat.getId(),
                chat.getStudent().getId(),
                chat.getMessage(),
                chat.getCreatedDate()
        );
    }
}
