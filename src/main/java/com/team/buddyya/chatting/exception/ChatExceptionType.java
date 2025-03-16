package com.team.buddyya.chatting.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ChatExceptionType implements BaseExceptionType {

    CHATROOM_NOT_FOUND(5001, HttpStatus.NOT_FOUND, "Chatroom not found."),
    USER_NOT_PART_OF_CHATROOM(5002, HttpStatus.FORBIDDEN, "User is not part of the chatroom."),
    SELF_CHAT_REQUEST_NOT_ALLOWED(5003, HttpStatus.BAD_REQUEST, "Cannot send a chat request to yourself."),
    CHAT_REQUEST_ALREADY_EXISTS(5004, HttpStatus.BAD_REQUEST, "Chat request already sent."),
    CHATROOM_ALREADY_EXISTS(5005, HttpStatus.BAD_REQUEST, "Chatroom already exists."),
    CHATROOM_ALREADY_EXITED(5006, HttpStatus.BAD_REQUEST, "This chat room has already been left.");

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
