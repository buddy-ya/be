package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.student.domain.Student;

public record CreateChatroomResponse(
        Long id,
        String name,
        Long buddyId,
        String buddyName,
        String country,
        String profileImageUrl
) {

    public static CreateChatroomResponse from(Chatroom chatroom, Student buddy) {
        return new CreateChatroomResponse(
                chatroom.getId(),
                chatroom.getName(),
                buddy.getId(),
                buddy.getName(),
                buddy.getCountry(),
                buddy.getProfileImage().getUrl()
        );
    }
}