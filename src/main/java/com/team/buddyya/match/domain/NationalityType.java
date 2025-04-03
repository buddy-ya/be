package com.team.buddyya.match.domain;

import com.team.buddyya.match.exception.MatchException;
import com.team.buddyya.match.exception.MatchExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NationalityType {

    KOREAN("KOREAN"),
    GLOBAL("GLOBAL");

    private final String displayName;

    NationalityType(String displayName) {
        this.displayName = displayName;
    }

    public static NationalityType fromValue(String value) {
        return Arrays.stream(NationalityType.values())
                .filter(type -> type.getDisplayName().equals(value))
                .findFirst()
                .orElseThrow(() -> new MatchException(MatchExceptionType.INVALID_MATCH_TYPE));
    }
}
