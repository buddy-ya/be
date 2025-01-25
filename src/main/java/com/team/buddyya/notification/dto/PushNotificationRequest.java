package com.team.buddyya.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PushNotificationRequest {
    private String to;
    private String title;
    private String body;
}