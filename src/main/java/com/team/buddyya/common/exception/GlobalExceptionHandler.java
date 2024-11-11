package com.team.buddyya.common.exception;

import com.team.buddyya.certification.exception.PhoneAuthenticationExceptionType;
import com.team.buddyya.certification.exception.PhoneAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handleBaseException(final BaseException e) {
        final BaseExceptionType baseExceptionType = e.exceptionType();
        final ExceptionResponse exceptionResponse = new ExceptionResponse(baseExceptionType.errorCode(), baseExceptionType.errorMessage());

        log.info("error = {}", exceptionResponse);

        return ResponseEntity.status(baseExceptionType.httpStatus()).body(exceptionResponse);
    }
}
