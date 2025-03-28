package com.team.buddyya.report.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ReportExceptionType implements BaseExceptionType {

    REPORT_NOT_FOUND(8000, HttpStatus.NOT_FOUND, "Report not found."),
    ALREADY_REPORTED(8001, HttpStatus.CONFLICT, "You have already reported this content.");


    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ReportExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
