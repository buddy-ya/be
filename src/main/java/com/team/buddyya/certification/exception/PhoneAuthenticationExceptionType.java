package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PhoneAuthenticationExceptionType implements BaseExceptionType {

    SMS_SEND_FAILED(1000, HttpStatus.UNAUTHORIZED, "Failed to send SMS."),
    PHONE_NOT_FOUND(1000, HttpStatus.NOT_FOUND, "Matching phone number not found."),
    PHONE_INFO_NOT_FOUND(1000, HttpStatus.NOT_FOUND, "Matching phone Info not found."),
    CODE_MISMATCH(1000, HttpStatus.UNAUTHORIZED, "Authentication code does not match."),
    MAX_SMS_SEND_COUNT(1005, HttpStatus.TOO_MANY_REQUESTS, "Too many SMS sent.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    PhoneAuthenticationExceptionType(final int errorCode,
                                     final HttpStatus httpStatus,
                                     final String errorMessage) {
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
