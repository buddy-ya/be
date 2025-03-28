package com.team.buddyya.match.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.match.domain.MatchRequest;
import com.team.buddyya.student.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;
import static com.team.buddyya.student.domain.UserProfileDefaultImage.isDefaultUserProfileImage;

public record MatchStatusResponse(
        Long id,
        Long chatRoomId,
        Long buddyId,
        String name,
        String country,
        String university,
        String gender,
        String profileImageUrl,
        List<String> majors,
        List<String> languages,
        List<String> interests,
        String matchStatus,
        boolean isExited
) {

    public static MatchStatusResponse from(Chatroom chatroom, Student buddy, MatchRequest matchRequest, boolean isExited) {
        return new MatchStatusResponse(
                matchRequest.getId(),
                chatroom.getId(),
                buddy.getId(),
                buddy.getName(),
                buddy.getCountry(),
                buddy.getUniversity().getUniversityName(),
                buddy.getGender().getDisplayName(),
                getChatroomProfileImage(buddy),
                convertToStringList(buddy.getMajors()),
                convertToStringList(buddy.getLanguages()),
                convertToStringList(buddy.getInterests()),
                matchRequest.getMatchRequestStatus().getDisplayName(),
                isExited
        );
    }

    public static MatchStatusResponse from(MatchRequest matchRequest) {
        return new MatchStatusResponse(
                matchRequest.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                matchRequest.getMatchRequestStatus().getDisplayName(),
                false
        );
    }

    public static MatchStatusResponse from(String status) {
        return new MatchStatusResponse(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                status,
                false
        );
    }

    private static List<String> convertToStringList(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
