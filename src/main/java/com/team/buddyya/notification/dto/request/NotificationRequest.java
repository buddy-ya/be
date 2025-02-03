package com.team.buddyya.notification.dto.request;

public record NotificationRequest(
        Long userId,
        String message
) {
}
