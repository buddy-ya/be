package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class EmailCertificateException extends BaseException {

    private final EmailCertificateExceptionType emailCertificateExceptionType;

    public EmailCertificateException(final EmailCertificateExceptionType emailCertificateExceptionType) {
        this.emailCertificateExceptionType = emailCertificateExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return emailCertificateExceptionType;
    }
}
