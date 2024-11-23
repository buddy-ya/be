package com.team.buddyya.admin.dto.request;

public record StudentVerificationRequest(
        Long studentId,
        String imageUrl,
        String studentNumber
) {
}
