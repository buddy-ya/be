package com.team.buddyya.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.auth.exception.AuthExceptionType;
import com.team.buddyya.common.exception.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        AuthExceptionType exceptionType = AuthExceptionType.UNAUTHORIZED_USER;
        response.setStatus(exceptionType.httpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(new ExceptionResponse(exceptionType.errorCode(), exceptionType.errorMessage())));
    }
}
