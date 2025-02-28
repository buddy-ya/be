package com.team.buddyya.match.dto.response;

public record MatchStatusResponse(String status) {

    public static MatchStatusResponse from(String status) {
        return new MatchStatusResponse(status);
    }
}
