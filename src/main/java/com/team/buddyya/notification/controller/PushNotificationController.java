package com.team.buddyya.notification.controller;

import com.team.buddyya.notification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    // Push Token 저장
    @PostMapping("/register")
    public ResponseEntity<String> registerPushToken(
            @RequestParam String userId,
            @RequestParam String token) {
        pushNotificationService.savePushToken(userId, token);
        return ResponseEntity.ok("Push token registered successfully");
    }

    // 푸시 알림 전송
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(
            @RequestParam String userId,
            @RequestParam String title,
            @RequestParam String body) {
        try {
            pushNotificationService.sendNotification(userId, title, body);
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send notification");
        }
    }
}
