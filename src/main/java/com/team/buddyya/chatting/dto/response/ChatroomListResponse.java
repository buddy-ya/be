package com.team.buddyya.chatting.dto.response;

import java.util.List;

public record ChatroomListResponse(
        List<ChatroomResponse> rooms,
        int totalUnreadCount
) {

    public static ChatroomListResponse from(List<ChatroomResponse> chatroomResponse, int totalUnreadCount) {
        return new ChatroomListResponse(chatroomResponse, totalUnreadCount);
    }
}
