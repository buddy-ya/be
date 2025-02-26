package com.team.buddyya.match.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.student.domain.Student;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;

public record MatchCreateResponse(
        Long id,
        Long buddyId,
        String name,
        String country,
        String profileImageUrl,
        Boolean isNew,
        String matchStatus
) {

    public static MatchCreateResponse from(Chatroom chatroom, Student buddy, boolean isNew, String matchStatus) {
        return new MatchCreateResponse(
                chatroom.getId(),
                buddy.getId(),
                buddy.getName(),
                buddy.getCountry(),
                getChatroomProfileImage(buddy),
                isNew,
                matchStatus
        );
    }

    public static MatchCreateResponse from(String matchStatus){
        return new MatchCreateResponse(
                null,
                null,
                null,
                null,
                null,
                null,
                matchStatus
        );
    }
}
