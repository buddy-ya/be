package com.team.buddyya.feed.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum FeedExceptionType implements BaseExceptionType {

    FEED_NOT_FOUND(4000, HttpStatus.NOT_FOUND, "Feed not found."),
    CATEGORY_NOT_FOUND(4000, HttpStatus.NOT_FOUND, "Category not found."),
    NOT_FEED_OWNER(4001, HttpStatus.FORBIDDEN, "You are not the owner of this feed."),
    FEED_ALREADY_LIKED(4002, HttpStatus.CONFLICT, "This post is already liked."),
    FEED_NOT_LIKED(4003, HttpStatus.NOT_FOUND, "This post has not been liked."),
    FEED_ALREADY_BOOKMARKED(4004, HttpStatus.CONFLICT, "This post is already bookmarked."),
    FEED_NOT_BOOKMARKED(4005, HttpStatus.NOT_FOUND, "This post has not been bookmarked."),
    COMMENT_NOT_FOUND(4006, HttpStatus.NOT_FOUND, "Comment not found."),
    NOT_COMMENT_OWNER(4007, HttpStatus.FORBIDDEN, "You are not the owner of this comment."),
    COMMENT_NOT_LIKED(4003, HttpStatus.NOT_FOUND, "This comment has not been liked."),
    COMMENT_DEPTH_LIMIT(4008, HttpStatus.FORBIDDEN, "Nested replies beyond this depth are not allowed.");

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
        return errorCode;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}
