package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum CertificateExceptionType implements BaseExceptionType {

    CODE_MISMATCH(1000, HttpStatus.UNAUTHORIZED, "Authentication code가 일치하지 않습니다"),
    ALREADY_CERTIFICATED(1002, HttpStatus.UNAUTHORIZED, "이미 인증된 상태입니다"),
    DUPLICATED_EMAIL_ADDRESS(1002, HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    STUDENT_EMAIL_NOT_FOUND(1003,HttpStatus.NOT_FOUND, "학생의 이메일을 찾을 수 없습니다."),
    STUDENT_ID_CARD_NOT_FOUND(1003, HttpStatus.NOT_FOUND, "학생증을 찾을 수 없습니다."),
    EMAIL_SEND_FAILED(1004, HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다.");

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
