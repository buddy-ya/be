package com.team.buddyya.notification.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.notification.dto.request.FeedNotificationRequest;
import com.team.buddyya.notification.dto.request.NotificationRequest;
import com.team.buddyya.notification.dto.response.NotificationResponse;
import com.team.buddyya.notification.dto.request.SaveTokenRequest;
import com.team.buddyya.notification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/save-token")
    public ResponseEntity<String> registerPushToken(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestBody SaveTokenRequest request) {
        pushNotificationService.savePushToken(userDetails.getStudentInfo().id(), request.token());
        return ResponseEntity.ok("토큰이 정상적으로 저장되었습니다.");
    }

    @PostMapping("/simple")
    public ResponseEntity<NotificationResponse> sendSimpleNotification(@RequestBody NotificationRequest request) {
        NotificationResponse response = pushNotificationService.sendSimpleNotification(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/feed")
    public ResponseEntity<NotificationResponse> sendFeedNotification(@RequestBody FeedNotificationRequest request) {
        NotificationResponse response = pushNotificationService.sendFeedNotification(request);
        return ResponseEntity.ok(response);
    }
}
