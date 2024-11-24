package com.team.buddyya.certification.dto.response;

public record CertificationStatusResponse(
        boolean isCertificated,
        boolean isStudentIdCardRequested
) {

    public static CertificationStatusResponse from(boolean isCertificated, boolean isStudentIdCardRequested) {
        return new CertificationStatusResponse(isCertificated, isStudentIdCardRequested);
    }
}
