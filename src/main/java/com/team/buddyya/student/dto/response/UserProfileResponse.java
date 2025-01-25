package com.team.buddyya.student.dto.response;

import com.team.buddyya.student.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

public record UserProfileResponse(
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
        List<String> interests
) {

    public static UserProfileResponse from(Student student, boolean isStudentIdCardRequested) {
        return new UserProfileResponse(
                student.getId(),
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
                student.getUserProfileImage(),
                student.getIsCertificated(),
                isStudentIdCardRequested,
                student.getIsKorean(),
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests())
        );
    }

    public static UserProfileResponse from(Student student) {
        return new UserProfileResponse(
                student.getId(),
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
                student.getUserProfileImage(),
                null,
                null,
                null,
                convertToStringList(student.getMajors()),
                convertToStringList(student.getLanguages()),
                convertToStringList(student.getInterests())
        );
    }

    private static List<String> convertToStringList(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
