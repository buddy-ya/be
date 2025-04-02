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
    MATCHING_PROFILE_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "Matching Profile not found."),
    INVALID_GENDER_VALUE(2000, HttpStatus.BAD_REQUEST, "Invalid gender value."),
    INVALID_DEFAULT_IMAGE_KEY(2000, HttpStatus.BAD_REQUEST, "Invalid profile image key."),
    INVALID_NAME_UPDATE_REQUEST(2001, HttpStatus.BAD_REQUEST, "Only one value is required for name update."),
    INVALID_INTRODUCTION_UPDATE_REQUEST(2001, HttpStatus.BAD_REQUEST, "Only one value is required for introduction update."),
    INVALID_ACTIVITY_UPDATE_REQUEST(2001, HttpStatus.BAD_REQUEST, "Only one value is required for activity update."),
    UNSUPPORTED_UPDATE_KEY(2002, HttpStatus.BAD_REQUEST, "Unsupported update key."),
    INVALID_INTRODUCTION_LENGTH(2003, HttpStatus.BAD_REQUEST, "Introduction must be at least 10 characters long."),
    INVALID_ACTIVITIES_LENGTH(2004, HttpStatus.BAD_REQUEST, "Activities must be at least 10 characters long."),
    CANNOT_BLOCK_SELF(2010, HttpStatus.BAD_REQUEST, "You cannot block yourself."),
    ALREADY_BLOCKED(2011, HttpStatus.CONFLICT, "User is already blocked."),
    INVITATION_CODE_NOT_FOUND(2012, HttpStatus.NOT_FOUND, "Invitation code not found."),
    INVALID_INVITATION_CODE(2013, HttpStatus.BAD_REQUEST, "This Invitation code is not valid."),
    INVITATION_EVENT_ALREADY_PARTICIPATED(2014, HttpStatus.BAD_REQUEST, "You have already joined the invitation event."),
    SELF_INVITATION_CODE(2015, HttpStatus.BAD_REQUEST, "You cannot use your own invitation code.");

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
