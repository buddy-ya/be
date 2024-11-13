package com.team.buddyya.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class ExceptionResponse {

    private int code;
    private String message;

    public ExceptionResponse(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ExceptionResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}