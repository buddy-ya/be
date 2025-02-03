package com.team.buddyya.chatting.dto.response;

public record ChatRequestInfoResponse(
        boolean isAlreadyExistChatRequest,
        boolean isAlreadyExistChatroom
) {

    public static ChatRequestInfoResponse from(boolean isAlreadyExistChatRequest, boolean isAlreadyExistChatroom) {
        return new ChatRequestInfoResponse(isAlreadyExistChatRequest, isAlreadyExistChatroom);
    }
}
