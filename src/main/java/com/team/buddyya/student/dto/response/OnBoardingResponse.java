package com.team.buddyya.student.dto.response;

public record OnBoardingResponse(
        String accessToken,
        String refreshToken
) {

    public static OnBoardingResponse from(String accessToken, String refreshToken) {
        return new OnBoardingResponse(
                accessToken,
                refreshToken
        );
    }
}
