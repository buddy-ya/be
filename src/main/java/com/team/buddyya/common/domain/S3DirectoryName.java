package com.team.buddyya.common.domain;

public enum S3DirectoryName {

    STUDENT_ID_CARD("/student-id-card"),
    FEED_IMAGE("/feeds"),
    CHAT_IMAGE("/chats"),
    PROFILE_IMAGE("/profile");

    private final String directoryName;

    S3DirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }
}
