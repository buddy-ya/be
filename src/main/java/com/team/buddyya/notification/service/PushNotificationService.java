package com.team.buddyya.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.notification.domain.PushToken;
import com.team.buddyya.notification.dto.PushNotificationRequest;
import com.team.buddyya.notification.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final PushTokenRepository pushTokenRepository;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${expo.api.url}")
    private String expoApiUrl;

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

    public void sendNotification(String userId, String title, String body) throws IOException {
        PushToken pushToken = pushTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Expo Push Notification payload
        String payload = objectMapper.writeValueAsString(
                new PushNotificationRequest(pushToken.getToken(), title, body));

        HttpPost postRequest = new HttpPost(expoApiUrl + "/push/send");
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
