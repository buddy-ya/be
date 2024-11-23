package com.team.buddyya.certification.dto.response;

public record CertificationStatusResponse(
        boolean isCertificated,
        boolean isStudentIdCardRequested
) {
}
