package com.team.buddyya.match.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MatchExceptionType implements BaseExceptionType {

    MATCH_REQUEST_NOT_FOUND(6000, HttpStatus.NOT_FOUND, "Match request not found."),
    INVALID_MATCH_TYPE(6001, HttpStatus.BAD_REQUEST, "Invalid match type."),
    UNEXPECTED_MATCH_STATUS(6002, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected match status encountered.");

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
