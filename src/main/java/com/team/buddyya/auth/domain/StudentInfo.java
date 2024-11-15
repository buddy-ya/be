package com.team.buddyya.auth.domain;

import com.team.buddyya.student.domain.Role;
import com.team.buddyya.student.domain.Student;

public record StudentInfo(
        Long id,
        Role role,
        Boolean isKorean
) {

    public static StudentInfo from(Student student) {
        return new StudentInfo(
                student.getId(),
                student.getRole(),
                student.getIsKorean()
        );
    }
}