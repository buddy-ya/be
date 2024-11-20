package com.team.buddyya.feed.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum FeedExceptionType implements BaseExceptionType {

    FEED_NOT_FOUND(400, HttpStatus.NOT_FOUND, "해당 피드를 찾지 못했습니다."),
    CATEGORY_NOT_FOUND(400, HttpStatus.NOT_FOUND, "해당 카테고리를 찾지 못했습니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    FeedExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int errorCode() {
        return errorCode();
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus();
    }

    @Override
    public String errorMessage() {
        return errorMessage();
    }
}
