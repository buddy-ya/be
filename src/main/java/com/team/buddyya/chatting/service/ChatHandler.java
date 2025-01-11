package com.team.buddyya.chatting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.chatting.dto.request.ChatRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final ChatService service;

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
        log.info("payload : {}", payload);

        ChatRequest chatMessage = mapper.readValue(payload, ChatRequest.class);
        log.info("session : {}", chatMessage.toString());

        service.handleAction(session, chatMessage);
    }
}
