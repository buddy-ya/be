package com.team.buddyya.match.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.match.domain.MatchRequest;
import com.team.buddyya.match.domain.MatchRequestStatus;
import com.team.buddyya.student.domain.Student;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;

public record MatchResponse(
        Long id,
        Long chatRoodId,
        Long buddyId,
        String name,
        String country,
        String university,
        String profileImageUrl,
        String matchStatus
) {

    public static MatchResponse from(Chatroom chatroom, Student buddy, MatchRequest matchRequest) {
        return new MatchResponse(
                matchRequest.getId(),
                chatroom.getId(),
                buddy.getId(),
                buddy.getName(),
                buddy.getCountry(),
                buddy.getUniversity().getUniversityName(),
                getChatroomProfileImage(buddy),
                matchRequest.getMatchRequestStatus().getDisplayName()
        );
    }

    public static MatchResponse from(MatchRequest matchRequest) {
        return new MatchResponse(
                matchRequest.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                matchRequest.getMatchRequestStatus().getDisplayName()
        );
    }

    public static MatchResponse from(String status) {
        return new MatchResponse(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                status
        );
    }
}
