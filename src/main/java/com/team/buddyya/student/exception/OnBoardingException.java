package com.team.buddyya.student.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class OnBoardingException extends BaseException {

    private final OnBoardingExceptionType exceptionType;

    public OnBoardingException(OnBoardingExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}

