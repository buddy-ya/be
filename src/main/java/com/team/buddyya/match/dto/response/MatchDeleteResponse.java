package com.team.buddyya.match.dto.response;

public record MatchDeleteResponse (boolean isExited){

    public static MatchDeleteResponse from(boolean isExited){
        return new MatchDeleteResponse(isExited);
    }
}
