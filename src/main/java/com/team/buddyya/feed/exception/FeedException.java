package com.team.buddyya.feed.exception;

import com.team.buddyya.common.exception.BaseException;
import com.team.buddyya.common.exception.BaseExceptionType;

public class FeedException extends BaseException {

    private final FeedExceptionType exceptionType;

    public FeedException(FeedExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
