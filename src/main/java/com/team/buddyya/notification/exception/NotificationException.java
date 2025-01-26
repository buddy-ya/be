package com.team.buddyya.notification.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class NotificationException extends BaseException {

    private final NotificationExceptionType exceptionType;

    public NotificationException(NotificationExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
