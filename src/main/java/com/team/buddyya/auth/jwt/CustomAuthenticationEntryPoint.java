package com.team.buddyya.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.auth.exception.AuthExceptionType;
import com.team.buddyya.common.exception.ExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        AuthExceptionType exceptionType = AuthExceptionType.ACCESS_DENIED;
        response.setStatus(exceptionType.httpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(new ExceptionResponse(exceptionType.errorCode(), exceptionType.errorMessage())));
    }
}