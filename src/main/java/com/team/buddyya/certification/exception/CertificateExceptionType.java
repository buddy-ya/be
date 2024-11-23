package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum CertificateExceptionType implements BaseExceptionType {

    CERTIFICATE_FAILED(101, HttpStatus.UNAUTHORIZED, "인증 중 오류 발생"),
    ALREADY_CERTIFICATED(102, HttpStatus.UNAUTHORIZED, "이미 인증된 상태입니다"),
    DUPLICATE_EMAIL(102, HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    STUDENT_ID_CARD_NOT_FOUND(103, HttpStatus.NOT_FOUND, "학생증을 찾을 수 없습니다.");

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
