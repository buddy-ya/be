package com.team.buddyya.common.exception;

import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PhoneAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handlePhoneAuthenticationException(PhoneAuthenticationException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = errorCode == ErrorCode.SMS_SEND_FAILED ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }
}
