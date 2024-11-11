package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PhoneAuthenticationExceptionType implements BaseExceptionType {

    SMS_SEND_FAILED(
            100,
            HttpStatus.UNAUTHORIZED,
            "SMS 전송 실패"
    ),
    CODE_MISMATCH(
            100,
            HttpStatus.UNAUTHORIZED,
            "Authentication code가 일치하지 않습니다"
    );

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
    PhoneAuthenticationExceptionType(final int errorCode,
                      final HttpStatus httpStatus,
                      final String errorMessage) {
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
