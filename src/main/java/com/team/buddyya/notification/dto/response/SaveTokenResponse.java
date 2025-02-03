package com.team.buddyya.notification.dto.response;

public record SaveTokenResponse(String message) {
    public static SaveTokenResponse from(String message) {
        return new SaveTokenResponse(message);
    }
}