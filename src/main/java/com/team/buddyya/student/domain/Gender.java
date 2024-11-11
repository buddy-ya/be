package com.team.buddyya.student.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Gender {
    MALE("male"),
    FEMALE("female"),
    UNKNOWN("unknown");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public static Gender fromValue(String displayName) {
        return Arrays.stream(Gender.values())
                .filter(gender -> gender.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid gender value: " + displayName));
    }
}
