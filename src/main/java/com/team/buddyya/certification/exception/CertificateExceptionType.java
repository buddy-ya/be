package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum CertificateExceptionType implements BaseExceptionType {

    CODE_MISMATCH(1000, HttpStatus.UNAUTHORIZED, "Authentication code does not match."),
    ALREADY_CERTIFICATED(1002, HttpStatus.UNAUTHORIZED, "Already certified."),
    DUPLICATED_EMAIL_ADDRESS(1002, HttpStatus.CONFLICT, "Email address already registered."),
    STUDENT_EMAIL_NOT_FOUND(1003, HttpStatus.NOT_FOUND, "Student email not found."),
    STUDENT_ID_CARD_NOT_FOUND(1003, HttpStatus.NOT_FOUND, "Student ID card not found."),
    EMAIL_SEND_FAILED(1004, HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    CertificateExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
