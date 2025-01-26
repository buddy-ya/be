package com.team.buddyya.notification.controller;

import com.team.buddyya.notification.dto.FeedNotificationRequest;
import com.team.buddyya.notification.dto.NotificationRequest;
import com.team.buddyya.notification.dto.NotificationResponse;
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

    // 토큰 저장 API
    @PostMapping("/register")
    public ResponseEntity<String> registerPushToken(
            @RequestParam String userId,
            @RequestParam String token) {
        pushNotificationService.savePushToken(userId, token);
        return ResponseEntity.ok("Push token registered successfully");
    }

    // 학생 인증, 매칭, 채팅 알림 API (RestTemplate 사용)
    @PostMapping("/simple")
    public ResponseEntity<NotificationResponse> sendSimpleNotification(@RequestBody NotificationRequest request) {
        NotificationResponse response = pushNotificationService.sendSimpleNotification(request);
        return ResponseEntity.ok(response);
    }

    // 피드 댓글/좋아요 알림 API (WebClient 사용)
    @PostMapping("/feed")
    public ResponseEntity<NotificationResponse> sendFeedNotification(@RequestBody FeedNotificationRequest request) {
        NotificationResponse response = pushNotificationService.sendFeedNotification(request);
        return ResponseEntity.ok(response);
    }
}
