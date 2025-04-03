package com.team.buddyya.match.dto.response;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.match.domain.MatchRequest;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.student.domain.MatchingProfile;
import com.team.buddyya.student.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;

public record MatchResponse(
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
        Integer point,
        Integer pointChange,
        String introduction,
        String buddyActivity,
        boolean isExited
) {

    public static MatchResponse from(Chatroom chatroom, Student buddy, MatchRequest matchRequest, Point point, boolean isExited, MatchingProfile matchingProfile) {
        return new MatchResponse(
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
                point.getCurrentPoint(),
                PointType.MATCH_REQUEST.getPointChange(),
                matchingProfile.getIntroduction(),
                matchingProfile.getBuddyActivity(),
                isExited
        );
    }

    public static MatchResponse from(MatchRequest matchRequest, Point point) {
        return new MatchResponse(
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
                point.getCurrentPoint(),
                PointType.MATCH_REQUEST.getPointChange(),
                null,
                null,
                false
        );
    }

    private static List<String> convertToStringList(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
