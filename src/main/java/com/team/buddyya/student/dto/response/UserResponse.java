package com.team.buddyya.student.dto.response;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;
import static com.team.buddyya.student.domain.UserProfileDefaultImage.isDefaultUserProfileImage;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.student.domain.MatchingProfile;
import com.team.buddyya.student.domain.Student;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record UserResponse(
        Long id,
        String role,
        String name,
        String country,
        String university,
        String gender,
        String profileImageUrl,
        Boolean isCertificated,
        Boolean isStudentIdCardRequested,
        Boolean isKorean,
        Boolean isDefaultProfileImage,
        List<String> majors,
        List<String> languages,
        List<String> interests,
        String status,
        Boolean isBanned,
        LocalDateTime banExpiration,
        String banReason,
        Integer point,
        Integer totalUnreadCount,
        String accessToken,
        String refreshToken,
        String introduction,
        String buddyActivity,
        Boolean isMatchingProfileCompleted,
        Boolean isMatchingActive,
        Boolean isFeedActive
) {

    public static UserResponse fromUserInfo(Student student, boolean isStudentIdCardRequested, Point point,
                                            Integer totalUnreadCount, MatchingProfile matchingProfile) {
        return new UserResponse(
                student.getId(),
                student.getRole().name(),
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
                student.getProfileImage().getUrl(),
                student.getIsCertificated(),
                isStudentIdCardRequested,
                student.getIsKorean(),
                isDefaultUserProfileImage(student),
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                null,
                student.checkAndUpdateBanStatus(),
                student.getBanEndTime(),
                student.getBanReason(),
                point.getCurrentPoint(),
                totalUnreadCount,
                null,
                null,
                matchingProfile.getIntroduction(),
                matchingProfile.getBuddyActivity(),
                matchingProfile.isCompleted(),
                student.getUniversity().getIsMatchingActive(),
                student.getUniversity().getIsFeedActive()
        );
    }

    public static UserResponse fromOtherUserInfo(Student student, MatchingProfile matchingProfile) {
        return new UserResponse(
                student.getId(),
                null,
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
                getChatroomProfileImage(student),
                student.getIsCertificated(),
                null,
                null,
                isDefaultUserProfileImage(student),
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                matchingProfile.getIntroduction(),
                matchingProfile.getBuddyActivity(),
                matchingProfile.isCompleted(),
                null,
                null
        );
    }

    public static UserResponse fromOnboard(Student student, Boolean isStudentIdCardRequested, String accessToken,
                                           String refreshToken, Point point) {
        return new UserResponse(
                student.getId(),
                student.getRole().name(),
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
                student.getProfileImage().getUrl(),
                student.getIsCertificated(),
                isStudentIdCardRequested,
                student.getIsKorean(),
                isDefaultUserProfileImage(student),
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                null,
                student.checkAndUpdateBanStatus(),
                student.getBanEndTime(),
                student.getBanReason(),
                point.getCurrentPoint(),
                null,
                accessToken,
                refreshToken,
                null,
                null,
                null,
                student.getUniversity().getIsMatchingActive(),
                student.getUniversity().getIsFeedActive()
        );
    }

    public static UserResponse fromCheckMembership(Student student, Boolean isStudentIdCardRequested, String status,
                                                   String accessToken, String refreshToken, Point point) {
        return new UserResponse(
                student.getId(),
                student.getRole().name(),
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
                student.getProfileImage().getUrl(),
                student.getIsCertificated(),
                isStudentIdCardRequested,
                student.getIsKorean(),
                isDefaultUserProfileImage(student),
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                status,
                student.checkAndUpdateBanStatus(),
                student.getBanEndTime(),
                student.getBanReason(),
                point.getCurrentPoint(),
                null,
                accessToken,
                refreshToken,
                null,
                null,
                null,
                student.getUniversity().getIsMatchingActive(),
                student.getUniversity().getIsFeedActive()
        );
    }

    public static UserResponse fromCheckMembership(String status) {
        return new UserResponse(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                true,
                null,
                null,
                null,
                status,
                false,
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
                null
        );
    }

    private static List<String> convertToStringList(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
