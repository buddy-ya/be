package com.team.buddyya.auth.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum AuthExceptionType implements BaseExceptionType {

    INVALID_TOKEN(300, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    UNSUPPORTED_TOKEN(300, HttpStatus.BAD_REQUEST, "지원되지 않는 토큰입니다."),
    EMPTY_CLAIMS(300, HttpStatus.BAD_REQUEST, "빈 토큰값입니다."),
    ACCESS_DENIED(300, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    UNAUTHORIZED_USER(301, HttpStatus.UNAUTHORIZED, "인가되지 않은 사용자입니다."),
    EXPIRED_TOKEN(302, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_MEMBER_ID(303, HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 ID 타입입니다."),
    REFRESH_TOKEN_NOT_FOUND(305, HttpStatus.NOT_FOUND, "리프레시 토큰을 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN(305, HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");

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
