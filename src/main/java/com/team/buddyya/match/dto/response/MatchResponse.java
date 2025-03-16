package com.team.buddyya.match.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.student.domain.Student;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;

public record MatchResponse(
        Long chatRoodId,
        Long buddyId,
        String name,
        String country,
        String university,
        String profileImageUrl,
        String matchStatus
) {

    public static MatchResponse from(Chatroom chatroom, Student buddy, String matchStatus) {
        return new MatchResponse(
                chatroom.getId(),
                buddy.getId(),
                buddy.getName(),
                buddy.getCountry(),
                buddy.getUniversity().getUniversityName(),
                getChatroomProfileImage(buddy),
                matchStatus
        );
    }

    public static MatchResponse from(String matchStatus){
        return new MatchResponse(
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
