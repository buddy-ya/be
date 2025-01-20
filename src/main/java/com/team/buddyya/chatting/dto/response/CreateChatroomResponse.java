package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.student.domain.Student;

public record CreateChatroomResponse(
        Long roomId,
        Long buddyId,
        String buddyName,
        String buddyCountry,
        String roomName,
        String profileUrl
) {

    public static CreateChatroomResponse from(Chatroom chatroom, Student buddy) {
        return new CreateChatroomResponse(
                chatroom.getId(),
                buddy.getId(),
                buddy.getName(),
                buddy.getCountry(),
                chatroom.getName(),
                buddy.getProfileImage().getUrl()
        );
    }
}