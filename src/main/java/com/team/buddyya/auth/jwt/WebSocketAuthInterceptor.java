package com.team.buddyya.auth.jwt;

import com.team.buddyya.chatting.repository.ChatroomStudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private static final String ERROR_UNAUTHORIZED = "권한 없음";
    private static final String ERROR_INTERNAL_SERVER = "핸드셰이크 중 내부 서버 오류 발생";
    private static final String ERROR_INVALID_JWT = "유효하지 않은 JWT 토큰";
    private static final String ERROR_UNAUTHORIZED_ACCESS = "방에 대한 권한 없음: ";
    private static final String ERROR_INVALID_AUTH_HEADER = "Authorization 헤더가 없거나 유효하지 않습니다";
    private static final String ERROR_INVALID_PATH_VARIABLE = "경로가 잘못되었습니다: 마지막 변수를 찾을 수 없습니다";

    private final JwtUtils jwtUtils;
    private final ChatroomStudentRepository chatroomStudentRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            validateHandshake(request, attributes);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Handshake failed: {}", e.getMessage());
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, ERROR_UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Internal server error during handshake: {}", e.getMessage(), e);
            setErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNAL_SERVER);
        }
        return false;
    }

    private void validateHandshake(ServerHttpRequest request, Map<String, Object> attributes) {
        String token = extractHeader(request, "Authorization").substring(7);
        Long roomId = extractLastPathVariableAsLong(request);
        Long studentId = jwtUtils.getUserId(token);
        validateTokenAndAccess(token, studentId, roomId);
        attributes.put("userId", studentId);
        attributes.put("roomId", roomId);
    }

    private void setErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            response.getBody().write(("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Failed to write response body: {}", e.getMessage());
        }
    }

    private String extractHeader(ServerHttpRequest request, String headerName) {
        String header = request.getHeaders().getFirst(headerName);
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException(ERROR_INVALID_AUTH_HEADER);
        }
        return header;
    }

    private Long extractLastPathVariableAsLong(ServerHttpRequest request) {
        return Arrays.stream(request.getURI().getPath().split("/"))
                .filter(segment -> !segment.isBlank())
                .reduce((first, second) -> second)
                .map(Long::valueOf)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_INVALID_PATH_VARIABLE));
    }

    private void validateTokenAndAccess(String token, Long studentId, Long roomId) {
        if (!jwtUtils.validateToken(token)) {
            throw new IllegalArgumentException(ERROR_INVALID_JWT);
        }
        if (!chatroomStudentRepository.existsByStudentIdAndChatroomId(studentId, roomId)) {
            throw new IllegalArgumentException(ERROR_UNAUTHORIZED_ACCESS + roomId);
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}