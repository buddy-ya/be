package com.team.buddyya.point.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PointExceptionType implements BaseExceptionType {

    INVALID_POINT_TYPE(2100, HttpStatus.BAD_REQUEST, "Invalid point type."),
    NEGATIVE_POINT(2101, HttpStatus.BAD_REQUEST, "Points cannot be less than zero."),
    EXCEED_MAX_POINT(2102, HttpStatus.BAD_REQUEST, "Exceeding the maximum allowable points is not permitted."),
    POINT_NOT_FOUND(2103, HttpStatus.NOT_FOUND, "The student's points do not exist.");

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
