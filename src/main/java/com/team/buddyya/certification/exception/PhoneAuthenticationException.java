package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class PhoneAuthenticationException extends BaseException {

    private final PhoneAuthenticationExceptionType phoneAuthenticationExceptionType;

    public PhoneAuthenticationException(final PhoneAuthenticationExceptionType phoneAuthenticationExceptionType) {
        this.phoneAuthenticationExceptionType = phoneAuthenticationExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return phoneAuthenticationExceptionType;
    }
}
