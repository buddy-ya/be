package com.team.buddyya.match.domain;

import com.team.buddyya.match.exception.MatchException;
import com.team.buddyya.match.exception.MatchExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MatchRequestStatus {

    MATCH_SUCCESS("success"),
    MATCH_PENDING("pending"),
    MATCH_NOT_REQUESTED("not_requested");

    private final String displayName;

    MatchRequestStatus(String displayName) {
        this.displayName = displayName;
    }
}
