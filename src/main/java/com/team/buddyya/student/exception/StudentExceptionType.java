package com.team.buddyya.student.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum StudentExceptionType implements BaseExceptionType {

    STUDENT_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "Student not found."),
    UNIVERSITY_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "University not found."),
    MAJOR_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "Major not found."),
    LANGUAGE_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "Language not found."),
    INTEREST_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "Interest not found."),
    PROFILE_IMAGE_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "Profile image not found."),
    INVALID_GENDER_VALUE(2000, HttpStatus.BAD_REQUEST, "Invalid gender value."),
    INVALID_DEFAULT_IMAGE_KEY(2000, HttpStatus.BAD_REQUEST, "Invalid profile image key."),
    INVALID_NAME_UPDATE_REQUEST(2001, HttpStatus.BAD_REQUEST, "Only one value is required for name update."),
    UNSUPPORTED_UPDATE_KEY(2002, HttpStatus.BAD_REQUEST, "Unsupported update key."),
    CANNOT_BLOCK_SELF(2010, HttpStatus.BAD_REQUEST, "You cannot block yourself."),
    ALREADY_BLOCKED(2011, HttpStatus.CONFLICT, "User is already blocked.");

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
