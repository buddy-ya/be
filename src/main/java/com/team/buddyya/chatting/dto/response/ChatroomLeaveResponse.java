package com.team.buddyya.chatting.dto.response;

public record ChatroomLeaveResponse(
        String message
) {

    public static ChatroomLeaveResponse from(String message) {
        return new ChatroomLeaveResponse(message);
    }
}
