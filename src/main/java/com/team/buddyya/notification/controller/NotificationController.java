package com.team.buddyya.notification.controller;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.notification.dto.request.PushToAllUsersRequest;
import com.team.buddyya.notification.dto.request.SaveTokenRequest;
import com.team.buddyya.notification.dto.response.SaveTokenResponse;
import com.team.buddyya.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/save-token")
    public ResponseEntity<SaveTokenResponse> registerPushToken(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @RequestBody SaveTokenRequest request) {
        return ResponseEntity.ok(notificationService.savePushToken(userDetails.getStudentInfo().id(), request.token()));
    }

    @PostMapping("/send-to-all")
    public ResponseEntity<Void> sendNotificationToAllUser(@RequestBody PushToAllUsersRequest request) {
        notificationService.sendNotificationToAllUser(request);
        return ResponseEntity.noContent().build();
    }
}
