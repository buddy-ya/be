package com.team.buddyya.chatting.dto.request;

import com.team.buddyya.chatting.domain.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatMessage {

    private MessageType type;
    private Long roomId;
    private Long userId;
    private String message;
    private Long tempId;
    private LocalDateTime time;

    @Builder
    public ChatMessage(MessageType type, Long roomId, Long userId, String message, Long tempId, LocalDateTime time) {
        this.type = type;
        this.roomId = roomId;
        this.userId = userId;
        this.message = message;
        this.tempId = tempId;
        this.time = time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}