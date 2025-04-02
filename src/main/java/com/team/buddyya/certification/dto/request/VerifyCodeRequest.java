package com.team.buddyya.certification.dto.request;
public record VerifyCodeRequest(
        String phoneNumber,
        String code,
        String udId) {
}
