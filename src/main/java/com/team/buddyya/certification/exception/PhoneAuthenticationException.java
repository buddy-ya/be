package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.ErrorCode;

public class PhoneAuthenticationException extends RuntimeException {
    private final ErrorCode errorCode;

    public PhoneAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
