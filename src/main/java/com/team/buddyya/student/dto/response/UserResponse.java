package com.team.buddyya.student.dto.response;

import com.team.buddyya.student.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

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
        List<String> majors,
        List<String> languages,
        List<String> interests,
        String status,
        String accessToken,
        String refreshToken
) {

    public static UserResponse from(Student student, boolean isStudentIdCardRequested) {
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
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                null,
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
                student.getProfileImage().getUrl(),
                null,
                null,
                null,
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                null,
                null,
                null
        );
    }

    public static UserResponse from(Student student, Boolean isStudentIdCardRequested, String accessToken, String refreshToken) {
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
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                null,
                accessToken,
                refreshToken
        );
    }

    public static UserResponse from(Student student, Boolean isStudentIdCardRequested, String status, String accessToken, String refreshToken) {
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
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests()),
                status,
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
                null,
                null,
                null,
                status,
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
