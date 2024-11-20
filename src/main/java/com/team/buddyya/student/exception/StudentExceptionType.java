package com.team.buddyya.student.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum StudentExceptionType implements BaseExceptionType {

    STUDENT_NOT_FOUND(200, HttpStatus.NOT_FOUND, "해당 학생을 찾지 못하였습니다."),
    UNIVERSITY_NOT_FOUND(200, HttpStatus.NOT_FOUND, "해당 대학을 찾지 못하였습니다."),
    MAJOR_NOT_FOUND(200, HttpStatus.NOT_FOUND, "해당 전공을 찾지 못하였습니다."),
    LANGUAGE_NOT_FOUND(200, HttpStatus.NOT_FOUND, "해당 언어를 찾지 못하였습니다."),
    INTEREST_NOT_FOUND(200, HttpStatus.NOT_FOUND, "해당 관심사를 찾지 못하였습니다."),
    INVALID_GENDER_VALUE(200, HttpStatus.BAD_REQUEST, "유효하지 않은 성별 값입니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    StudentExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
