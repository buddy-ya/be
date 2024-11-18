package com.team.buddyya.common.domain;

public enum S3DirectoryName {

    STUDENT_ID_CARD("/student-id-card");

    private final String dir;

    S3DirectoryName(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }
}
