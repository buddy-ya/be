package com.team.buddyya.student.dto.response;

import com.team.buddyya.point.domain.Point;
import com.team.buddyya.student.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;
import static com.team.buddyya.student.domain.UserProfileDefaultImage.isDefaultUserProfileImage;

public record UserResponse(
        Long id,
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
        Integer point,
        String accessToken,
        String refreshToken
) {

    public static UserResponse from(Student student, boolean isStudentIdCardRequested, Point point) {
        return new UserResponse(
                student.getId(),
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
                student.getIsBanned(),
                point.getCurrentPoint(),
                null,
                null
        );
    }

    public static UserResponse from(Student student) {
        return new UserResponse(
                student.getId(),
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
                getChatroomProfileImage(student),
                null,
                null,
                null,
                isDefaultUserProfileImage(student),
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                null,
                student.getIsBanned(),
                null,
                null,
                null
        );
    }

    public static UserResponse from(Student student, Boolean isStudentIdCardRequested, String accessToken, String refreshToken, Point point) {
        return new UserResponse(
                student.getId(),
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
                student.getIsBanned(),
                point.getCurrentPoint(),
                accessToken,
                refreshToken
        );
    }

    public static UserResponse from(Student student, Boolean isStudentIdCardRequested, String status, String accessToken, String refreshToken, Point point) {
        return new UserResponse(
                student.getId(),
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
                student.getIsBanned(),
                point.getCurrentPoint(),
                accessToken,
                refreshToken
        );
    }

    public static UserResponse from(String status) {
        return new UserResponse(
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
                null
        );
    }

    private static List<String> convertToStringList(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
