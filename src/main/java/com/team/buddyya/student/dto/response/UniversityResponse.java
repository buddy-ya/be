package com.team.buddyya.student.dto.response;

import com.team.buddyya.student.domain.University;

public record UniversityResponse(String university) {

    public static UniversityResponse from(University university) {
        return new UniversityResponse(university.getUniversityName());
    }
}
