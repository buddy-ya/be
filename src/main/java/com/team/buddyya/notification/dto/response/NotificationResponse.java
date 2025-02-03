package com.team.buddyya.notification.dto.response;

public record NotificationResponse (String message){

    public static NotificationResponse from(String message) {
        return new NotificationResponse(message);
    }
}