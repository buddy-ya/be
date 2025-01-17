package com.team.buddyya.student.dto.response;

import com.team.buddyya.student.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

public record MyPageResponse(
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

    public static MyPageResponse from(Student student, boolean isStudentIdCardRequested) {
        return new MyPageResponse(
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
                convertToStringList(student.getInterests())
        );
    }

    private static List<String> convertToStringList(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
