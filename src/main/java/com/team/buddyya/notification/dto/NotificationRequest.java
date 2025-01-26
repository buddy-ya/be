package com.team.buddyya.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationRequest {
    private String userId; // 알림 받는 유저 ID
    private String message; // 알림 메시지
}
