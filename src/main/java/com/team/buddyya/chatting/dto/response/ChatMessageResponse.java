package com.team.buddyya.chatting.dto.response;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long senderId,
        String message,
        LocalDateTime createdDate
) {
}
