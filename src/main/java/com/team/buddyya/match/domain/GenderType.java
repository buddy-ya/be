package com.team.buddyya.match.domain;

import com.team.buddyya.match.exception.MatchException;
import com.team.buddyya.match.exception.MatchExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GenderType {

    SAME_GENDER("SAME"),
    ALL_GENDER("ALL");

    private final String displayName;

    GenderType(String displayName) {
        this.displayName = displayName;
    }

    public static GenderType fromValue(String value) {
        return Arrays.stream(GenderType.values())
                .filter(type -> type.displayName.equals(value))
                .findFirst()
                .orElseThrow(() -> new MatchException(MatchExceptionType.INVALID_MATCH_TYPE));
    }
}
