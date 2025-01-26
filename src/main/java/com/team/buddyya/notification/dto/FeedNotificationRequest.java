package com.team.buddyya.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedNotificationRequest {
    private Long feedId; // 피드 ID
    private String message; // 알림 메시지
}