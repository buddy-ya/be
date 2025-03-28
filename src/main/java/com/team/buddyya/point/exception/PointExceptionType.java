package com.team.buddyya.point.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PointExceptionType implements BaseExceptionType {
    POINT_NOT_FOUND(10000, HttpStatus.NOT_FOUND, "The student's points do not exist."),
    INVALID_POINT_TYPE(10001, HttpStatus.BAD_REQUEST, "Invalid point type."),
    NEGATIVE_POINT(10002, HttpStatus.BAD_REQUEST, "Points cannot be less than zero."),
    EXCEED_MAX_POINT(10003, HttpStatus.BAD_REQUEST, "Exceeding the maximum allowable points is not permitted.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    PointExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
