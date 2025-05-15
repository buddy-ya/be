package com.team.buddyya.match.exception;

import com.team.buddyya.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MatchExceptionType implements BaseExceptionType {

    MATCH_REQUEST_NOT_FOUND(6000, HttpStatus.NOT_FOUND, "Match request not found."),
    INVALID_MATCH_TYPE(6001, HttpStatus.BAD_REQUEST, "Invalid match type."),
    UNEXPECTED_MATCH_STATUS(6002, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected match status encountered."),
    MATCH_PROFILE_NOT_FOUND(6003, HttpStatus.NOT_FOUND, "Matching profile not found."),
    MATCH_PROFILE_NOT_COMPLETED(6004, HttpStatus.BAD_REQUEST, "Matching profile is not completed."),
    MATCH_HISTORY_NOT_FOUND(6005, HttpStatus.NOT_FOUND, "Recent matched history not found."),
    MATCH_REQUEST_TIME_INVALID(6006, HttpStatus.BAD_REQUEST, "You can only request a match twice per day.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    MatchExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
