package com.team.buddyya.common.exception;

public enum ErrorCode {
    SMS_SEND_FAILED("E2001", "Failed to send SMS"),
    CODE_MISMATCH("E2002", "Authentication code mismatch");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
