package com.team.buddyya.chatting.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ChatExceptionType implements BaseExceptionType {

    SELF_CHAT_REQUEST_NOT_ALLOWED(5002, HttpStatus.BAD_REQUEST, "본인에게 채팅 요청을 할 수 없습니다."),
    CHATROOM_NOT_FOUND(5001, HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    USER_NOT_PART_OF_CHATROOM(5002, HttpStatus.FORBIDDEN, "사용자가 채팅방에 속해 있지 않습니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ChatExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int errorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}
