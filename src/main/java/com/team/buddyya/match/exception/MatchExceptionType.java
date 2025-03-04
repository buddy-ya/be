package com.team.buddyya.match.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MatchExceptionType implements BaseExceptionType {

    INVALID_MATCH_TYPE(6000, HttpStatus.BAD_REQUEST, "유효하지 않은 매칭 타입입니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    MatchExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
