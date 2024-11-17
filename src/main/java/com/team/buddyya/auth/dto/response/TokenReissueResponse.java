package com.team.buddyya.auth.dto.response;

public record TokenReissueResponse(
        String accessToken,
        String refreshToken
) {
}
