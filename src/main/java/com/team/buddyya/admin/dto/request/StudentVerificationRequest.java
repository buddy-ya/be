package com.team.buddyya.admin.dto.request;

public record StudentVerificationRequest(
        Long id,
        boolean isApproved,
        String rejectionReason,
        String imageUrl
) {
}
