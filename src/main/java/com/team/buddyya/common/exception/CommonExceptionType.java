package com.team.buddyya.common.exception;

import org.springframework.http.HttpStatus;

public enum CommonExceptionType implements BaseExceptionType {

    FILE_UPLOAD_ERROR(9000, HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred during file upload.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    CommonExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
