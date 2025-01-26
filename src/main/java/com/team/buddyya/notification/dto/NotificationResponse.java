package com.team.buddyya.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private boolean success; // 전송 성공 여부
    private String message;  // 응답 메시지
}