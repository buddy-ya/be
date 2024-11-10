package com.team.buddyya.certification.exception;

public enum PhoneAuthenticationErrorCode {

    SMS_SEND_FAILED("E2001", "SMS 전송 실패"),
    CODE_MISMATCH("E2002", "Authentication code가 일치하지 않습니다");

    private final String code;
    private final String message;

    PhoneAuthenticationErrorCode(String code, String message) {
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
