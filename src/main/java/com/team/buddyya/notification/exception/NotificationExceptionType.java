package com.team.buddyya.notification.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum NotificationExceptionType implements BaseExceptionType {

    TOKEN_NOT_FOUND(7001, HttpStatus.NOT_FOUND, "User token not found."),
    NOTIFICATION_SEND_FAILED(7002, HttpStatus.BAD_REQUEST, "Failed to send notification.");

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
