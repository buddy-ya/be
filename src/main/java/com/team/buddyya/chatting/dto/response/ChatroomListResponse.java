package com.team.buddyya.chatting.dto.response;

import java.util.List;

public record ChatroomListResponse(
        List<ChatroomResponse> rooms,
        int totalUnreadCount,
        boolean hasChatRequest
) {

    public static ChatroomListResponse from(List<ChatroomResponse> chatroomResponse, int totalUnreadCount, boolean hasChatRequest) {
        return new ChatroomListResponse(chatroomResponse, totalUnreadCount, hasChatRequest);
    }
}
