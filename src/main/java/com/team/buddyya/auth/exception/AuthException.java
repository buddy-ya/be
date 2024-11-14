package com.team.buddyya.auth.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class AuthException extends BaseException {

    private final AuthExceptionType authExceptionType;

    public AuthException(final AuthExceptionType authExceptionType) {
        this.authExceptionType = authExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return authExceptionType;
    }
}
