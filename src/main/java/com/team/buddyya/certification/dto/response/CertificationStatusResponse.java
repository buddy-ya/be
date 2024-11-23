package com.team.buddyya.certification.dto.response;

public record CertificationStatusResponse(
        boolean success,
        boolean isStudentIdCardRequested
) {
}
