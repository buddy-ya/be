package com.team.buddyya.student.dto.response;

import com.team.buddyya.student.domain.Student;

import java.util.List;
import java.util.stream.Collectors;

public record MyPageResponse(
        String name,
        String country,
        String university,
        String gender,
        List<String> majors,
        List<String> languages,
        List<String> interests
) {

    public static MyPageResponse from(Student student) {
        return new MyPageResponse(
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getGender().getDisplayName(),
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
