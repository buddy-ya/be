package com.team.buddyya.auth.domain;

import com.team.buddyya.student.domain.Role;
import com.team.buddyya.student.domain.Student;

public record StudentInfo(
        Long id,
        String name,
        Role role
) {

    public static StudentInfo from(Student student) {
        return new StudentInfo(
                student.getId(),
                student.getName(),
                student.getRole()
        );
    }
}