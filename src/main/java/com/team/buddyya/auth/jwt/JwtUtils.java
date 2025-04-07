package com.team.buddyya.auth.jwt;

import com.team.buddyya.auth.dto.request.TokenInfoRequest;
import com.team.buddyya.auth.exception.AuthException;
import com.team.buddyya.auth.exception.AuthExceptionType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtUtils {

    private final Key key;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public JwtUtils(@Value("${auth.jwt.secret}") String secretKey,
                    @Value("${auth.jwt.access.expiration}") long accessTokenExpireTime,
                    @Value("${auth.jwt.refresh.expiration}") long refreshTokenExpireTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public String createAccessToken(TokenInfoRequest context) {
        return createToken(context, accessTokenExpireTime);
    }

    public String createRefreshToken(TokenInfoRequest context) {
        return createToken(context, refreshTokenExpireTime);
    }

    private String createToken(TokenInfoRequest context, long expiredTime) {
        Claims claims = Jwts.claims();
        claims.put("studentId", context.userId());
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expiredTime);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthExceptionType.EXPIRED_TOKEN);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new AuthException(AuthExceptionType.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthExceptionType.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new AuthException(AuthExceptionType.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new AuthException(AuthExceptionType.EMPTY_CLAIMS);
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        Object studentId = claims.get("studentId");
        if (studentId instanceof Number) {
            return ((Number) studentId).longValue();
        }
        throw new AuthException(AuthExceptionType.INVALID_MEMBER_ID);
    }
}