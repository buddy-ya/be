package com.team.buddyya.student.dto.response;

import com.team.buddyya.student.domain.Student;

public record OnBoardingResponse(
        String name,
        String country,
        String university,
        String major,
        String accessToken,
        String refreshToken
) {

    public static OnBoardingResponse from(Student student, String accessToken, String refreshToken) {
        return new OnBoardingResponse(
                student.getName(),
                student.getCountry(),
                student.getUniversity().getUniversityName(),
                student.getMajor(),
                accessToken,
                refreshToken
        );
    }
}
