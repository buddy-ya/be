package com.team.buddyya.common.domain;

public enum S3DirectoryName {

    STUDENT_ID_CARD("/student-id-card"),
    FEED_IMAGE("/feeds");

    private final String directoryName;

    S3DirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }
}
