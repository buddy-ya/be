package com.team.buddyya.auth.dto;

import com.team.buddyya.student.domain.Role;
import com.team.buddyya.student.domain.Student;

public record LoginStudentInfo(
        Long id,
        String name,
        Role role
) {

    public static LoginStudentInfo from(Student student) {
        return new LoginStudentInfo(
                student.getId(),
                student.getName(),
                student.getRole()
        );
    }
}