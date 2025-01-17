package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.student.domain.Student;

public record CreateChatroomResponse(
        Long chatroomId,
        Long buddyId,
        String buddyName,
        String buddyCountry,
        String roomName,
        String profileUrl,
        Long postId
) {

    public static CreateChatroomResponse from(Chatroom chatroom, Student buddy) {
        return new CreateChatroomResponse(
                chatroom.getId(),
                buddy.getId(),
                buddy.getName(),
                buddy.getCountry(),
                chatroom.getName(),
                buddy.getProfileImage().getUrl(),
                chatroom.getPostId()
        );
    }
}