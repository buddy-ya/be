package com.team.buddyya.auth.service;

import com.team.buddyya.auth.domain.AuthToken;
import com.team.buddyya.auth.dto.request.TokenInfoRequest;
import com.team.buddyya.auth.dto.request.TokenReissueRequest;
import com.team.buddyya.auth.dto.response.TokenReissueResponse;
import com.team.buddyya.auth.exception.AuthException;
import com.team.buddyya.auth.exception.AuthExceptionType;
import com.team.buddyya.auth.jwt.JwtUtils;
import com.team.buddyya.auth.repository.AuthTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final int REFRESH_TOKEN_THRESHOLD_DAYS = 30;

    private final JwtUtils jwtUtils;
    private final AuthTokenRepository authTokenRepository;

    public TokenReissueResponse reissueToken(TokenReissueRequest request) {
        jwtUtils.validateToken(request.refreshToken());
        Claims claims = jwtUtils.parseClaims(request.refreshToken());
        Long studentId = claims.get("id", Long.class);
        AuthToken authToken = authTokenRepository.findByStudentId(studentId)
                .orElseThrow(() -> new AuthException(AuthExceptionType.REFRESH_TOKEN_NOT_FOUND));
        if (!authToken.getRefreshToken().equals(request.refreshToken())) {
            throw new AuthException(AuthExceptionType.INVALID_REFRESH_TOKEN);
        }
        if (isTokenExpiringWithinDays(claims)) {
            return reissueAccessAndRefreshToken(studentId, authToken);
        }
        return reissueAccessToken(studentId);
    }

    private boolean isTokenExpiringWithinDays(Claims claims) {
        ZonedDateTime expirationDate = claims.getExpiration().toInstant().atZone(ZonedDateTime.now().getZone());
        long daysUntilExpiration = ChronoUnit.DAYS.between(ZonedDateTime.now(), expirationDate);
        return daysUntilExpiration <= REFRESH_TOKEN_THRESHOLD_DAYS;
    }

    private TokenReissueResponse reissueAccessAndRefreshToken(Long studentId, AuthToken authToken) {
        String newAccessToken = jwtUtils.createAccessToken(new TokenInfoRequest(studentId));
        String newRefreshToken = jwtUtils.createRefreshToken(new TokenInfoRequest(studentId));
        authToken.updateRefreshToken(newRefreshToken);
        return new TokenReissueResponse(newAccessToken, newRefreshToken);
    }

    private TokenReissueResponse reissueAccessToken(Long studentId) {
        String newAccessToken = jwtUtils.createAccessToken(new TokenInfoRequest(studentId));
        return new TokenReissueResponse(newAccessToken, null);
    }
}