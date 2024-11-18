package com.team.buddyya.certification.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class CertificateException extends BaseException {

    private final CertificateExceptionType certificateExceptionType;

    public CertificateException(final CertificateExceptionType certificateExceptionType) {
        this.certificateExceptionType = certificateExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return certificateExceptionType;
    }
}
