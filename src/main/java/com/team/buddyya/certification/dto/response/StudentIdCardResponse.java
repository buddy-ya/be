package com.team.buddyya.certification.dto.response;

public record StudentIdCardResponse(
        String studentIdCardUrl,
        String rejectionReason
) {
    public static StudentIdCardResponse from(String studentIdCardUrl, String rejectionReason) {
        return new StudentIdCardResponse(studentIdCardUrl, rejectionReason);
    }
}
