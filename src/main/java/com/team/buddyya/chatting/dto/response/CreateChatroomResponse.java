package com.team.buddyya.chatting.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.student.domain.Student;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;

public record CreateChatroomResponse(
        Long id,
        Long buddyId,
        String name,
        String country,
        String profileImageUrl,
        boolean isNew
        ) {

    public static CreateChatroomResponse from(Chatroom chatroom, Student buddy, boolean isNew) {
        return new CreateChatroomResponse(
                chatroom.getId(),
                buddy.getId(),
                buddy.getName(),
                buddy.getCountry(),
                getChatroomProfileImage(buddy),
                isNew
        );
    }
}