package com.team.buddyya.chatting.dto.request;

import com.team.buddyya.chatting.domain.MessageType;
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
    private LocalDateTime time;

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}