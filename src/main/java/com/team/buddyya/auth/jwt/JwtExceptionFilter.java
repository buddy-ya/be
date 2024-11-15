package com.team.buddyya.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.buddyya.auth.exception.AuthException;
import com.team.buddyya.common.exception.ExceptionResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AuthException ex) {
            response.setStatus(ex.exceptionType().httpStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter()
                    .write(objectMapper.writeValueAsString(new ExceptionResponse(ex.exceptionType().errorCode(), ex.exceptionType().errorMessage())));
        }
    }
}