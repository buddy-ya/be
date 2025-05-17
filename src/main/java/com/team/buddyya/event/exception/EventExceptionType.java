package com.team.buddyya.event.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum EventExceptionType implements BaseExceptionType {

    COUPON_NOT_FOUND(12000, HttpStatus.NOT_FOUND, "The coupon does not exist."),
    COUPON_ALREADY_USED(12001, HttpStatus.BAD_REQUEST, "The coupon has already been used.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    EventExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
