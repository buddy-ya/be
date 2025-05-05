package com.team.buddyya.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.feed.domain.Comment;
import com.team.buddyya.feed.domain.Feed;
import com.team.buddyya.match.domain.MatchRequest;
import com.team.buddyya.notification.domain.ExpoToken;
import com.team.buddyya.notification.domain.RequestNotification;
import com.team.buddyya.notification.dto.response.SaveTokenResponse;
import com.team.buddyya.notification.exception.NotificationException;
import com.team.buddyya.notification.exception.NotificationExceptionType;
import com.team.buddyya.notification.repository.ExpoTokenRepository;
import com.team.buddyya.point.domain.Point;
import com.team.buddyya.point.domain.PointType;
import com.team.buddyya.student.domain.MatchingProfile;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.team.buddyya.student.domain.UserProfileDefaultImage.getChatroomProfileImage;

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

    private static final String FEED_REPLY_TITLE_KR = "새로운 대댓글이 달렸어요!";
    private static final String FEED_REPLY_TITLE_EN = "A new reply has been added!";

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

    private static final String CHAT_REQUEST_TITLE_KR = "채팅 요청이 도착했어요!";
    private static final String CHAT_REQUEST_TITLE_EN = "You have a new chat request!";

    private static final String CHAT_REQUEST_BODY_KR = "님이 채팅 요청을 보냈습니다. 확인하고 대화를 시작해 보세요!";
    private static final String CHAT_REQUEST_BODY_EN = " has sent you a chat request. Check it out and start a conversation!";

    private static final String MATCH_SUCCESS_TITLE_KR = "새로운 매칭이 이루어졌습니다!";
    private static final String MATCH_SUCCESS_TITLE_EN = "A new match has been made!";

    private static final String MATCH_SUCCESS_BODY_KR = "지금 바로 채팅을 시작해 보세요.";
    private static final String MATCH_SUCCESS_BODY_EN = "Start chatting now.";

    private static final String CHAT_ACCEPT_TITLE_KR = "님이 채팅 요청을 수락했습니다.";
    private static final String CHAT_ACCEPT_TITLE_EN = "have accepted your chat request.";

    private static final String CHAT_ACCEPT_BODY_KR = "대화를 시작해보세요!";
    private static final String CHAT_ACCEPT_BODY_EN = "Start the conversation now!";

    private static final String INVITATION_REWARD_TITLE_KR = "포인트가 지급되었어요!";
    private static final String INVITATION_REWARD_TITLE_EN = "You have received points!";

    private static final String INVITATION_REWARD_BODY_KR = "추천 이벤트 참여로 포인트가 적립되었어요.";
    private static final String INVITATION_REWARD_BODY_EN = "You received points from a referral event.";

    private static final String REFUND_POINTS_TITLE_KR = "포인트가 환급되었어요!";
    private static final String REFUND_POINTS_TITLE_EN = "Your points have been refunded!";

    private static final String REFUND_POINTS_BODY_KR = "채팅 상대방의 미응답을 확인하여 포인트를 환급해드렸어요.";
    private static final String REFUND_POINTS_BODY_EN = "We confirmed no response from your chat partner and refunded your points.";

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

    public void sendMatchSuccessNotification(Student student, Long roomId) {
        try {
            String token = getTokenByUserId(student.getId());
            Map<String, Object> data = Map.of(
                    "roomId", roomId,
                    "wasPending", false,
                    "type", "MATCH"
            );
            boolean isKorean = student.getIsKorean();
            String title = getMatchSuccessNotificationTitle(isKorean);
            String body = getMatchSuccessNotificationBody(isKorean);
            sendToExpo(RequestNotification.builder()
                    .to(token)
                    .title(title)
                    .body(body)
                    .data(data).build()
            );
        } catch (NotificationException e) {
            log.warn("매칭 성공 알림 전송 실패: {}", e.exceptionType().errorMessage());
        }
    }

    public void sendPendingMatchSuccessNotification(Student student, Student buddy, Chatroom chatroom, MatchRequest matchRequest, Point point, boolean isExited, MatchingProfile matchingProfile) {
        try {
            String token = getTokenByUserId(student.getId());
            Map<String, Object> data = Map.ofEntries(
                    Map.entry("id", matchRequest.getId()),
                    Map.entry("chatRoomId", chatroom.getId()),
                    Map.entry("buddyId", buddy.getId()),
                    Map.entry("name", buddy.getName()),
                    Map.entry("country", buddy.getCountry()),
                    Map.entry("university", buddy.getUniversity().getUniversityName()),
                    Map.entry("gender", buddy.getGender().getDisplayName()),
                    Map.entry("profileImageUrl", getChatroomProfileImage(buddy)),
                    Map.entry("majors", convertToStringList(buddy.getMajors())),
                    Map.entry("languages", convertToStringList(buddy.getLanguages())),
                    Map.entry("interests", convertToStringList(buddy.getInterests())),
                    Map.entry("matchStatus", matchRequest.getMatchRequestStatus().getDisplayName()),
                    Map.entry("point", point.getCurrentPoint()),
                    Map.entry("pointChange", PointType.MATCH_REQUEST.getPointChange()),
                    Map.entry("introduction", matchingProfile.getIntroduction()),
                    Map.entry("buddyActivity", matchingProfile.getBuddyActivity()),
                    Map.entry("isExited", isExited),
                    Map.entry("wasPending", true),
                    Map.entry("type", "MATCH")
            );
            boolean isKorean = student.getIsKorean();
            String title = getMatchSuccessNotificationTitle(isKorean);
            String body = getMatchSuccessNotificationBody(isKorean);
            sendToExpo(RequestNotification.builder()
                    .to(token)
                    .title(title)
                    .body(body)
                    .data(data).build()
            );
        } catch (NotificationException e) {
            log.warn("매칭 성공 알림 전송 실패: {}", e.exceptionType().errorMessage());
        }
    }

    private String getMatchSuccessNotificationTitle(boolean isKorean) {
        return isKorean ? MATCH_SUCCESS_TITLE_KR : MATCH_SUCCESS_TITLE_EN;
    }

    private String getMatchSuccessNotificationBody(boolean isKorean) {
        return isKorean ? MATCH_SUCCESS_BODY_KR : MATCH_SUCCESS_BODY_EN;
    }

    public void sendCommentReplyNotification(Long writerId, Feed feed, Comment parent, String commentContent) {
        boolean isWriterParent = parent.isParent(writerId);
        boolean isParentFeedOwner = parent.isParent(feed.getStudent().getId());
        if (!isWriterParent && !isParentFeedOwner) {
            try {
                Student recipient = parent.getStudent();
                String token = getTokenByUserId(recipient.getId());
                Map<String, Object> data = Map.of(
                        "feedId", feed.getId(),
                        "type", "FEED"
                );
                boolean isKorean = recipient.getIsKorean();
                String title = getCommentReplyNotificationTitle(isKorean);
                sendToExpo(RequestNotification.builder()
                        .to(token)
                        .title(title)
                        .body(commentContent)
                        .data(data).build()
                );
            } catch (NotificationException e) {
                log.warn("피드 대댓글 알림 전송 실패: {}", e.exceptionType().errorMessage());
            }
        }
    }

    private String getCommentReplyNotificationTitle(boolean isKorean) {
        return isKorean ? FEED_REPLY_TITLE_KR : FEED_REPLY_TITLE_EN;
    }

    public void sendCommentNotification(Long writerId, Feed feed, String commentContent) {
        boolean isFeedOwner = feed.isFeedOwner(writerId);
        if (!isFeedOwner) {
            try {
                Student recipient = feed.getStudent();
                String token = getTokenByUserId(recipient.getId());
                Map<String, Object> data = Map.of(
                        "feedId", feed.getId(),
                        "type", "FEED"
                );
                boolean isKorean = recipient.getIsKorean();
                String title = getCommentNotificationTitle(isKorean);
                sendToExpo(RequestNotification.builder()
                        .to(token)
                        .title(title)
                        .body(commentContent)
                        .data(data).build()
                );
            } catch (NotificationException e) {
                log.warn("피드 알림 전송 실패: {}", e.exceptionType().errorMessage());
            }
        }
    }

    private String getCommentNotificationTitle(boolean isKorean) {
        return isKorean ? FEED_TITLE_KR : FEED_TITLE_EN;
    }

    public void sendAuthorizationNotification(Student student, Point point, boolean isSuccess) {
        try {
            String token = getTokenByUserId(student.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("type", "AUTHORIZATION");
            data.put("isCertificated", isSuccess);
            data.put("point", point.getCurrentPoint());
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
        return RequestNotification.builder()
                .to(token)
                .title(title)
                .body(body)
                .data(data)
                .build();
    }

    private String getTitle(boolean isKorean, boolean isSuccess) {
        if (isSuccess) {
            return getSuccessTitle(isKorean);
        }
        return getFailTitle(isKorean);
    }

    private String getBody(boolean isKorean, boolean isSuccess) {
        if (isSuccess) {
            return getSuccessBody(isKorean);
        }
        return getFailBody(isKorean);
    }

    private String getSuccessTitle(boolean isKorean) {
        return isKorean ? AUTH_SUCCESS_TITLE_KR : AUTH_SUCCESS_TITLE_EN;
    }

    private String getFailTitle(boolean isKorean) {
        return isKorean ? AUTH_FAIL_TITLE_KR : AUTH_FAIL_TITLE_EN;
    }

    private String getSuccessBody(boolean isKorean) {
        return isKorean ? AUTH_SUCCESS_BODY_KR : AUTH_SUCCESS_BODY_EN;
    }

    private String getFailBody(boolean isKorean) {
        return isKorean ? AUTH_FAIL_BODY_KR : AUTH_FAIL_BODY_EN;
    }

    public void sendChatRequestNotification(Student sender, Student receiver) {
        try {
            String token = getTokenByUserId(receiver.getId());
            boolean isKorean = receiver.getIsKorean();
            String title = getChatRequestTitle(isKorean);
            String body = getChatRequestBody(isKorean, sender.getName());
            Map<String, Object> data = Map.of(
                    "type", "CHAT_REQUEST"
            );
            sendToExpo(RequestNotification.builder()
                    .to(token)
                    .title(title)
                    .body(body)
                    .data(data)
                    .build()
            );
        } catch (NotificationException e) {
            log.warn("채팅 요청 알림 전송 실패: {}", e.exceptionType().errorMessage());
        }
    }

    private String getChatRequestTitle(boolean isKorean) {
        return isKorean ? CHAT_REQUEST_TITLE_KR : CHAT_REQUEST_TITLE_EN;
    }

    private String getChatRequestBody(boolean isKorean, String senderName) {
        return isKorean
                ? senderName + CHAT_REQUEST_BODY_KR
                : senderName + CHAT_REQUEST_BODY_EN;
    }

    public void sendChatAcceptNotification(Student student, String senderName, Long roomId) {
        try {
            String token = getTokenByUserId(student.getId());
            Map<String, Object> data = Map.of(
                    "roomId", roomId,
                    "type", "CHAT_ACCEPT"
            );
            boolean isKorean = student.getIsKorean();
            String title = senderName + " " + getChatAcceptTitle(isKorean);
            String body = getChatAcceptBody(isKorean);
            sendToExpo(RequestNotification.builder()
                    .to(token)
                    .title(title)
                    .body(body)
                    .data(data).build()
            );
        } catch (NotificationException e) {
            log.warn("채팅 수락 알림 전송 실패: {}", e.exceptionType().errorMessage());
        }
    }

    private String getChatAcceptTitle(boolean isKorean) {
        return isKorean ? CHAT_ACCEPT_TITLE_KR : CHAT_ACCEPT_TITLE_EN;
    }

    private String getChatAcceptBody(boolean isKorean) {
        return isKorean ? CHAT_ACCEPT_BODY_KR : CHAT_ACCEPT_BODY_EN;
    }

    public void sendInvitationRewardNotification(Point point, Student student) {
        try {
            String token = getTokenByUserId(student.getId());
            boolean isKorean = student.getIsKorean();
            String title = isKorean ? INVITATION_REWARD_TITLE_KR : INVITATION_REWARD_TITLE_EN;
            String body = isKorean ? INVITATION_REWARD_BODY_KR : INVITATION_REWARD_BODY_EN;
            Map<String, Object> data = Map.of(
                    "type", "POINT",
                    "point", point.getCurrentPoint()
            );
            sendToExpo(RequestNotification.builder()
                    .to(token)
                    .title(title)
                    .body(body)
                    .data(data)
                    .build()
            );
        } catch (NotificationException e) {
            log.warn("추천인 포인트 지급 알림 전송 실패: {}", e.exceptionType().errorMessage());
        }
    }

    public void sendRefundNotification(Point point, Student student) {
        try {
            String token = getTokenByUserId(student.getId());
            boolean isKorean = student.getIsKorean();
            String title = isKorean ? REFUND_POINTS_TITLE_KR : REFUND_POINTS_TITLE_EN;
            String body = isKorean ? REFUND_POINTS_BODY_KR : REFUND_POINTS_BODY_EN;
            Map<String, Object> data = Map.of(
                    "type", "POINT",
                    "point", point.getCurrentPoint()
            );
            sendToExpo(RequestNotification.builder()
                    .to(token)
                    .title(title)
                    .body(body)
                    .data(data)
                    .build()
            );
        } catch (NotificationException e) {
            log.warn("무응답 포인트 환급 알림 전송 실패: {}", e.exceptionType().errorMessage());
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

    private static List<String> convertToStringList(List<?> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
