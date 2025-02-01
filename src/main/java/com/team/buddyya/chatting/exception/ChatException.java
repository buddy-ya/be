package com.team.buddyya.chatting.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class ChatException extends BaseException {

    private final ChatExceptionType chatExceptionType;

    public ChatException(ChatExceptionType chatExceptionType) {
        this.chatExceptionType = chatExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return chatExceptionType;
    }
}
