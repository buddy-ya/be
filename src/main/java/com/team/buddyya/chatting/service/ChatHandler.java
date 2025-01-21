package com.team.buddyya.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.chatting.dto.request.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler {

    private static final String ERROR_INVALID_MESSAGE_FORMAT = "잘못된 메시지 형식 또는 필수 필드 누락";
    private static final String ERROR_MESSAGE_PROCESS_FAILURE = "메시지 처리 실패";
    private static final String ERROR_UNEXPECTED = "예기치 못한 오류 발생";
    private static final String ERROR_TRANSPORT = "전송 오류 발생";

    private final ObjectMapper mapper;
    private final ChatService service;

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            String payload = (String) message.getPayload();
            log.info("수신된 페이로드: {}", payload);
            ChatMessage chatMessage = mapper.readValue(payload, ChatMessage.class);
            chatMessage.setTime(LocalDateTime.now());
            service.handleAction(session, chatMessage);
        } catch (IllegalArgumentException e) {
            handleClientError(session, ERROR_INVALID_MESSAGE_FORMAT);
        } catch (IOException e) {
            handleClientError(session, ERROR_MESSAGE_PROCESS_FAILURE);
        } catch (Exception e) {
            handleServerError(session, ERROR_UNEXPECTED);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        Long roomId = (Long) session.getAttributes().get("roomId");
        if (userId == null || roomId == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        service.addSessionToRoom(roomId, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("세션 {}의 전송 오류: {}", session.getId(), exception.getMessage());
        closeSession(session, ERROR_TRANSPORT);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("세션 {}이(가) 상태 {}로 종료되었습니다.", session.getId(), status);
        service.removeSession(session);
    }

    private void handleClientError(WebSocketSession session, String errorMessage) {
        log.warn("클라이언트 오류: {}", errorMessage);
        sendErrorMessage(session, errorMessage, CloseStatus.BAD_DATA);
    }

    private void handleServerError(WebSocketSession session, String errorMessage) {
        log.error("서버 오류: {}", errorMessage);
        sendErrorMessage(session, errorMessage, CloseStatus.SERVER_ERROR);
    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage, CloseStatus closeStatus) {
        try {
            session.sendMessage(new TextMessage("{\"error\": \"" + errorMessage + "\"}"));
        } catch (IOException e) {
            log.error("세션 {}에 오류 메시지 전송 실패: {}", session.getId(), e.getMessage());
        } finally {
            closeSession(session, errorMessage);
        }
    }

    private void closeSession(WebSocketSession session, String reason) {
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
            log.error("세션 {} 종료 실패: {}", session.getId(), e.getMessage());
        }
    }
}
