package com.team.buddyya.notification.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum NotificationExceptionType implements BaseExceptionType {

    TOKEN_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "해당 유저의 토큰이 존재하지 않습니다."),
    FEED_NOT_FOUND(3002, HttpStatus.NOT_FOUND, "Feed not found."),
    NOTIFICATION_SEND_FAILED(3003, HttpStatus.BAD_REQUEST, "Failed to send notification.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    NotificationExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
