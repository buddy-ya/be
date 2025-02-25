package com.team.buddyya.feed.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum FeedExceptionType implements BaseExceptionType {

    FEED_NOT_FOUND(4000, HttpStatus.NOT_FOUND, "해당 피드를 찾지 못했습니다."),
    CATEGORY_NOT_FOUND(4000, HttpStatus.NOT_FOUND, "해당 카테고리를 찾지 못했습니다."),
    NOT_FEED_OWNER(4001, HttpStatus.FORBIDDEN, "해당 피드의 글쓴이가 아닙니다."),
    FEED_ALREADY_LIKED(4002, HttpStatus.CONFLICT, "이미 좋아요를 누른 게시글입니다."),
    FEED_NOT_LIKED(4003, HttpStatus.NOT_FOUND, "좋아요를 누르지 않은 게시글입니다."),
    FEED_ALREADY_BOOKMARKED(4004, HttpStatus.CONFLICT, "이미 북마크한 게시글입니다."),
    FEED_NOT_BOOKMARKED(4005, HttpStatus.NOT_FOUND, "북마크하지 않은 게시글입니다."),
    COMMENT_NOT_FOUND(4006, HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    NOT_COMMENT_OWNER(4007, HttpStatus.FORBIDDEN, "해당 댓글의 작성자가 아닙니다."),
    COMMENT_NOT_LIKED(4003, HttpStatus.NOT_FOUND, "좋아요를 누르지 않는 댓글입니다."),
    COMMENT_DEPTH_LIMIT(4008, HttpStatus.FORBIDDEN, "대댓글의 대댓글은 작성할 수 없습니다.");


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
