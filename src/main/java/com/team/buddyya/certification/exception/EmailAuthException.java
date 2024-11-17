package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class EmailAuthException extends BaseException {

    private final EmailAuthExceptionType emailAuthExceptionType;

    public EmailAuthException(final EmailAuthExceptionType emailAuthExceptionType) {
        this.emailAuthExceptionType = emailAuthExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return emailAuthExceptionType;
    }
}
