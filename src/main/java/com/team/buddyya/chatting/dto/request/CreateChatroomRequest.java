package com.team.buddyya.chatting.dto.request;

public record CreateChatroomRequest(
        Long buddyId,
        Long postId,
        String postName
) {
}
