package com.team.buddyya.report.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class ReportException extends BaseException {

    private final ReportExceptionType reportExceptionType;

    public ReportException(ReportExceptionType reportExceptionType) {
        this.reportExceptionType = reportExceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return reportExceptionType;
    }
}
