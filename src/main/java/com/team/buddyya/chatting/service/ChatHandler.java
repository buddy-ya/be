package com.team.buddyya.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.chatting.dto.request.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
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
            if ("PONG".equalsIgnoreCase(payload.trim())) {
                service.updateLastPongTimestamp(session);
                return;
            }
            ChatMessage chatMessage = mapper.readValue(payload, ChatMessage.class);
            chatMessage.setTime(LocalDateTime.now());
            service.handleAction(chatMessage);
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
        closeSession(session, ERROR_TRANSPORT);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        service.removeSession(session);
    }

    private void handleClientError(WebSocketSession session, String errorMessage) {
        sendErrorMessage(session, errorMessage, CloseStatus.BAD_DATA);
    }

    private void handleServerError(WebSocketSession session, String errorMessage) {
        sendErrorMessage(session, errorMessage, CloseStatus.SERVER_ERROR);
    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage, CloseStatus closeStatus) {
        try {
            session.sendMessage(new TextMessage("{\"error\": \"" + errorMessage + "\"}"));
        } catch (IOException e) {
        } finally {
            closeSession(session, errorMessage);
        }
    }

    private void closeSession(WebSocketSession session, String reason) {
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (IOException e) {
        }
    }
}
