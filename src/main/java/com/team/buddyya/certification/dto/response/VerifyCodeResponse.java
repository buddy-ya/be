package com.team.buddyya.certification.dto.response;

public record VerifyCodeResponse(
        String phoneNumber,
        String status,
        String accessToken,
        String refreshToken) {
}
