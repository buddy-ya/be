package com.team.buddyya.event.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class EventException extends BaseException {

    private final EventExceptionType exceptionType;

    public EventException(EventExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
