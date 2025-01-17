package com.team.buddyya.chatting.dto.request;

import com.team.buddyya.chatting.domain.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class ChatMessage {

    private MessageType type;
    private Long roomId;
    private Long userId;
    private String message;
    private LocalDateTime time;
}