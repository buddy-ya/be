package com.team.buddyya.student.dto.response;

import com.team.buddyya.student.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

public record UserPageResponse(
        String name,
        String country,
        String university,
        String gender,
        String profileImageUrl,
        List<String> majors,
        List<String> languages,
        List<String> interests
) {

    public static UserPageResponse from(Student student) {
        return new UserPageResponse(
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
                student.getProfileImage().getUrl(),
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
