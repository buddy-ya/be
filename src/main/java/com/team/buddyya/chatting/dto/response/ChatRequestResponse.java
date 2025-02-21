package com.team.buddyya.chatting.dto.response;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;

import com.team.buddyya.chatting.domain.ChatRequest;
import java.time.LocalDateTime;

public record ChatRequestResponse(
        Long id,
        Long senderId,
        String university,
        String name,
        String country,
        String profileImageUrl,
        LocalDateTime createdDate
) {

    public static ChatRequestResponse from(ChatRequest chatRequest) {
        return new ChatRequestResponse(
                chatRequest.getId(),
                chatRequest.getSender().getId(),
                chatRequest.getSender().getUniversity().getUniversityName(),
                chatRequest.getSender().getName(),
                chatRequest.getSender().getCountry(),
                getChatroomProfileImage(chatRequest.getSender()),
                chatRequest.getCreatedDate()
        );
    }
}
