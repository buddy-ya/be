package com.team.buddyya.chatting.dto.request;

import com.team.buddyya.chatting.domain.MessageType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ChatRequest(
        MessageType type,  // 메시지 타입
        Long roomId,       // 방 번호
        Long studentId,    // 발신자 ID
        String message,    // 메시지 내용
        String time        // 메시지 발송 시간
) {

    public ChatRequest(MessageType type, Long roomId, Long studentId, String message) {
        this(
                type,
                roomId,
                studentId,
                message,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
    }
}