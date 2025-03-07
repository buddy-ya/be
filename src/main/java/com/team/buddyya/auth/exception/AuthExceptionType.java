package com.team.buddyya.auth.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum AuthExceptionType implements BaseExceptionType {

    INVALID_TOKEN(3000, HttpStatus.UNAUTHORIZED, "Invalid token."),
    UNSUPPORTED_TOKEN(3000, HttpStatus.BAD_REQUEST, "Unsupported token."),
    EMPTY_CLAIMS(3000, HttpStatus.BAD_REQUEST, "Empty token value."),
    ACCESS_DENIED(3000, HttpStatus.FORBIDDEN, "Access denied."),
    UNAUTHORIZED_USER(3001, HttpStatus.UNAUTHORIZED, "Unauthorized user."),
    EXPIRED_TOKEN(3002, HttpStatus.UNAUTHORIZED, "Expired token."),
    INVALID_MEMBER_ID(3003, HttpStatus.BAD_REQUEST, "Invalid user ID type."),
    REFRESH_TOKEN_NOT_FOUND(3005, HttpStatus.NOT_FOUND, "Refresh token not found."),
    INVALID_REFRESH_TOKEN(3005, HttpStatus.UNAUTHORIZED, "Invalid refresh token.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    AuthExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
