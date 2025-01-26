package com.team.buddyya.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.notification.domain.PushToken;
import com.team.buddyya.notification.dto.FeedNotificationRequest;
import com.team.buddyya.notification.dto.NotificationRequest;
import com.team.buddyya.notification.dto.NotificationResponse;
import com.team.buddyya.notification.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final PushTokenRepository pushTokenRepository;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    // 토큰 저장
    public void savePushToken(String userId, String token) {
        pushTokenRepository.findByUserId(userId).ifPresentOrElse(
                existing -> existing.setToken(token),
                () -> {
                    PushToken pushToken = new PushToken();
                    pushToken.setUserId(userId);
                    pushToken.setToken(token);
                    pushTokenRepository.save(pushToken);
                }
        );
    }

    // 학생 인증, 매칭, 채팅 알림 전송 (RestTemplate 사용)
    public NotificationResponse sendSimpleNotification(NotificationRequest request) {
        try {
            String token = getUserToken(request.getUserId());
            sendNotificationToToken(token, request.getMessage());
            return new NotificationResponse(true, "Notification sent successfully");
        } catch (Exception e) {
            return new NotificationResponse(false, e.getMessage());
        }
    }

    // 피드 댓글/좋아요 알림 전송 (WebClient 사용)
    public NotificationResponse sendFeedNotification(FeedNotificationRequest request) {
        try {
            String token = getFeedAuthorToken(request.getFeedId());
            sendNotificationToToken(token, request.getMessage());
            return new NotificationResponse(true, "Notification sent successfully to feed author");
        } catch (Exception e) {
            return new NotificationResponse(false, e.getMessage());
        }
    }

    // 유저 ID로 Expo Push Token 가져오기
    private String getUserToken(String userId) {
        return pushTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .getToken();
    }

    // 피드 작성자의 Expo Push Token 가져오기
    private String getFeedAuthorToken(Long feedId) {
        // 실제 구현에서는 FeedRepository에서 피드 작성자의 정보를 가져옴
        // 여기선 간단히 예제를 위해 작성
        return "ExponentPushToken[example1]";
    }

    // 알림 전송 로직
    private void sendNotificationToToken(String token, String message) throws IOException {
        String payload = objectMapper.writeValueAsString(new NotificationRequest(token, message));
        HttpPost postRequest = new HttpPost(EXPO_PUSH_URL);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setEntity(new StringEntity(payload));

        httpClient.execute(postRequest, response -> {
            if (response.getCode() != HttpStatus.OK.value()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to send notification");
            }
            return null;
        });
    }
}
