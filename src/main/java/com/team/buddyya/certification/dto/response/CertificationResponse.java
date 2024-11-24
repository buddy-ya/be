package com.team.buddyya.certification.dto.response;

public record CertificationResponse(boolean success) {

    public static CertificationResponse from(boolean success) {
        return new CertificationResponse(success);
    }
}
