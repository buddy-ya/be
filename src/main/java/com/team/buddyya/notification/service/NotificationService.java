package com.team.buddyya.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.feed.exception.FeedException;
import com.team.buddyya.feed.exception.FeedExceptionType;
import com.team.buddyya.feed.respository.FeedRepository;
import com.team.buddyya.notification.domain.ExpoToken;
import com.team.buddyya.notification.domain.RequestNotification;
import com.team.buddyya.notification.dto.request.FeedNotificationRequest;
import com.team.buddyya.notification.dto.request.NotificationRequest;
import com.team.buddyya.notification.dto.response.NotificationResponse;
import com.team.buddyya.notification.dto.response.SaveTokenResponse;
import com.team.buddyya.notification.exception.NotificationException;
import com.team.buddyya.notification.exception.NotificationExceptionType;
import com.team.buddyya.notification.repository.ExpoTokenRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final ExpoTokenRepository expoTokenRepository;
    private final FeedRepository feedRepository;
    private final FindStudentService findStudentService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String NOTIFICATION_SUCCESS_MESSAGE = "알림이 성공적으로 전송되었습니다.";
    private static final String TOKEN_SAVE_SUCCESS_MESSAGE = "토큰이 성공적으로 저장되었습니다.";

    @Value("${EXPO.API.URL}")
    private String expoApiUrl;

    public SaveTokenResponse savePushToken(Long userId, String token) {
        Student student = findStudentService.findByStudentId(userId);
        expoTokenRepository.findByUserId(userId).ifPresentOrElse(
                existingToken -> updateExistingToken(existingToken, token),
                () -> saveToken(student, token)
        );
        return SaveTokenResponse.from(TOKEN_SAVE_SUCCESS_MESSAGE);
    }

    private void updateExistingToken(ExpoToken existingToken, String token) {
        existingToken.updateToken(token);
        expoTokenRepository.save(existingToken);
    }

    private void saveToken(Student student, String token) {
        ExpoToken Token = ExpoToken.builder()
                .token(token)
                .student(student)
                .build();
        expoTokenRepository.save(Token);
    }

    public NotificationResponse sendSimpleNotification(NotificationRequest request) {
        String token = expoTokenRepository.findByUserId(request.userId())
                .orElseThrow(() -> new NotificationException(NotificationExceptionType.TOKEN_NOT_FOUND))
                .getToken();
        sendNotificationToToken(token, request.message());
        return NotificationResponse.from(NOTIFICATION_SUCCESS_MESSAGE);
    }

    public NotificationResponse sendFeedNotification(FeedNotificationRequest request) {
        Feed feed = feedRepository.findById(request.feedId())
                .orElseThrow(() -> new FeedException(FeedExceptionType.FEED_NOT_FOUND));
        String token = expoTokenRepository.findByUserId(feed.getStudent().getId())
                .orElseThrow(() -> new NotificationException(NotificationExceptionType.TOKEN_NOT_FOUND))
                .getToken();
        sendNotificationToToken(token, request.message());
        return NotificationResponse.from(NOTIFICATION_SUCCESS_MESSAGE);
    }

    private void sendNotificationToToken(String token, String message) {
        try {
            String payload = objectMapper.writeValueAsString(new RequestNotification(token, message));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(expoApiUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NotificationException(NotificationExceptionType.NOTIFICATION_SEND_FAILED);
            }
        } catch (Exception e) {
            throw new NotificationException(NotificationExceptionType.NOTIFICATION_SEND_FAILED);
        }
    }

}
