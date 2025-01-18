package com.team.buddyya.chatting.dto.response;

public record LeaveChatroomResponse(
        String message
) {

    public static LeaveChatroomResponse from(String message) {
        return new LeaveChatroomResponse(message);
    }
}
