package com.team.buddyya.mission.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MissionExceptionType implements BaseExceptionType {

    TODAY_ALREADY_ATTENDED(11000, HttpStatus.BAD_REQUEST, "Attendance Mission already participated.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    MissionExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
