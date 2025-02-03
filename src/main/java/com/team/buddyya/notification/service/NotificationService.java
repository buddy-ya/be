package com.team.buddyya.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.notification.domain.ExpoToken;
import com.team.buddyya.notification.domain.RequestNotification;
import com.team.buddyya.notification.dto.response.SaveTokenResponse;
import com.team.buddyya.notification.exception.NotificationException;
import com.team.buddyya.notification.exception.NotificationExceptionType;
import com.team.buddyya.notification.repository.ExpoTokenRepository;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final ExpoTokenRepository expoTokenRepository;
    private final FindStudentService findStudentService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOKEN_SAVE_SUCCESS_MESSAGE = "토큰이 성공적으로 저장되었습니다.";

    private static final String FEED_TITLE_KR = "새로운 댓글이 달렸어요!";
    private static final String FEED_TITLE_EN = "You have a new comment!";

    private static final String AUTH_SUCCESS_TITLE_KR = "학생 인증이 성공했어요.";
    private static final String AUTH_SUCCESS_TITLE_EN = "Your student verification was successful.";

    private static final String AUTH_SUCCESS_BODY_KR = "이제 버디야의 모든 기능을 사용할 수 있어요.";
    private static final String AUTH_SUCCESS_BODY_EN = "You can now use all features of Buddyya.";

    private static final String AUTH_FAIL_TITLE_KR = "학생 인증에 실패했어요.";
    private static final String AUTH_FAIL_TITLE_EN = "Your student verification failed.";

    private static final String AUTH_FAIL_BODY_KR = "제출된 학생증 정보를 다시 확인하고 재시도해 주세요.";
    private static final String AUTH_FAIL_BODY_EN = "Please check your student ID information and try again.";

    @Value("${EXPO.API.URL}")
    private String expoApiUrl;

    public SaveTokenResponse savePushToken(Long userId, String token) {
        Student student = findStudentService.findByStudentId(userId);
        expoTokenRepository.findByStudentId(userId).ifPresentOrElse(
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

    public void sendFeedNotification(Feed feed, String commentContent) {
        try {
            Student recipient = feed.getStudent();
            String token = getTokenByUserId(recipient.getId());
            Map<String, Object> data = Map.of(
                    "feedId", feed.getId(),
                    "type", "FEED"
            );
            boolean isKorean = recipient.getIsKorean();
            String title = getFeedNotificationTitle(isKorean);
            RequestNotification notification = new RequestNotification(token, title, commentContent, data);
            sendToExpo(notification);
        } catch (NotificationException e) {
            log.warn("피드 알림 전송 실패: {}", e.exceptionType().errorMessage());
        }
    }

    private String getFeedNotificationTitle(boolean isKorean) {
        return isKorean ? FEED_TITLE_KR : FEED_TITLE_EN;
    }

    public void sendAuthorizationNotification(Student student, boolean isSuccess) {
        try {
            String token = getTokenByUserId(student.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("type", "AUTHORIZATION");
            boolean isKorean = student.getIsKorean();
            RequestNotification notification = createAuthorizationNotification(isKorean, isSuccess, token, data);
            sendToExpo(notification);
        } catch (NotificationException e) {
            log.warn("학생 인증 알림 전송 실패: {}", e.exceptionType().errorMessage());
        }
    }

    private RequestNotification createAuthorizationNotification(boolean isKorean, boolean isSuccess, String token, Map<String, Object> data) {
        String title = getTitle(isKorean, isSuccess);
        String body = getBody(isKorean, isSuccess);
        return new RequestNotification(token, title, body, data);
    }

    private String getTitle(boolean isKorean, boolean isSuccess) {
        if (isSuccess) {
            if (isKorean) {
                return AUTH_SUCCESS_TITLE_KR;
            } else {
                return AUTH_SUCCESS_TITLE_EN;
            }
        } else {
            if (isKorean) {
                return AUTH_FAIL_TITLE_KR;
            } else {
                return AUTH_FAIL_TITLE_EN;
            }
        }
    }

    private String getBody(boolean isKorean, boolean isSuccess) {
        if (isSuccess) {
            if (isKorean) {
                return AUTH_SUCCESS_BODY_KR;
            } else {
                return AUTH_SUCCESS_BODY_EN;
            }
        } else {
            if (isKorean) {
                return AUTH_FAIL_BODY_KR;
            } else {
                return AUTH_FAIL_BODY_EN;
            }
        }
    }

    private void sendToExpo(RequestNotification notification) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            String payload = objectMapper.writeValueAsString(notification);
            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(expoApiUrl, request, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("알림 전송 실패 (HTTP 에러): {}", e.getMessage());
            throw new NotificationException(NotificationExceptionType.NOTIFICATION_SEND_FAILED);
        } catch (Exception e) {
            log.error("알림 전송 실패 (알 수 없는 오류): {}", e.getMessage());
            throw new NotificationException(NotificationExceptionType.NOTIFICATION_SEND_FAILED);
        }
    }

    private String getTokenByUserId(Long userId) {
        return expoTokenRepository.findByStudentId(userId)
                .orElseThrow(() -> new NotificationException(NotificationExceptionType.TOKEN_NOT_FOUND))
                .getToken();
    }
}
