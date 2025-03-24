package com.team.buddyya.match.domain;

import com.team.buddyya.match.exception.MatchException;
import com.team.buddyya.match.exception.MatchExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GenderType {

    MALE("MALE"),
    FEMALE("FEMALE"),
    ALL("ALL");

    private final String displayName;

    GenderType(String displayName) {
        this.displayName = displayName;
    }

    public static GenderType fromValue(String value) {
        return Arrays.stream(GenderType.values())
                .filter(type -> type.displayName.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new MatchException(MatchExceptionType.INVALID_MATCH_TYPE));
    }
}
