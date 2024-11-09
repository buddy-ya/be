package com.team.buddyya.certification.exception;

public class PhoneAuthenticationException extends RuntimeException {

    private final PhoneAuthenticationErrorCode phoneAuthenticationErrorCode;

    public PhoneAuthenticationException(PhoneAuthenticationErrorCode phoneAuthenticationErrorCode) {
        super(phoneAuthenticationErrorCode.getMessage());
        this.phoneAuthenticationErrorCode = phoneAuthenticationErrorCode;
    }

    public PhoneAuthenticationErrorCode getErrorCode() {
        return phoneAuthenticationErrorCode;
    }
}
