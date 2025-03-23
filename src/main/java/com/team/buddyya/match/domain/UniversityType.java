package com.team.buddyya.match.domain;

import com.team.buddyya.match.exception.MatchException;
import com.team.buddyya.match.exception.MatchExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UniversityType {

    SAME_UNIVERSITY("SAME"),
    DIFFERENT_UNIVERSITY("DIFFERENT");

    private final String displayName;

    UniversityType(String displayName) {
        this.displayName = displayName;
    }

    public static UniversityType fromValue(String value) {
        return Arrays.stream(UniversityType.values())
                .filter(type -> type.getDisplayName().equals(value))
                .findFirst()
                .orElseThrow(() -> new MatchException(MatchExceptionType.INVALID_MATCH_TYPE));
    }
}
