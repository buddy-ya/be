package com.team.buddyya.admin.dto.request;

public record StudentVerificationRequest(
        Long id,
        String imageUrl,
        String studentNumber
) {
}
