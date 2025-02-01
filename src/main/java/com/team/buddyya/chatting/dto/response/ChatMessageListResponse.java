package com.team.buddyya.chatting.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ChatMessageListResponse(
        List<ChatMessageResponse> messages,
        int currentPage,
        int totalPages,
        boolean hasNext
) {
    public static ChatMessageListResponse from(Page<ChatMessageResponse> page) {
        return new ChatMessageListResponse(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}