package com.team.buddyya.student.domain;

import lombok.Getter;

@Getter
public enum UserProfileDefaultImage {

    USER_PROFILE_DEFAULT_IMAGE("userProfileDefaultImage", "기본 이미지 url");

    private final String key;
    private final String url;

    UserProfileDefaultImage(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public static boolean isDefaultUserProfileImage(Student student) {
        String studentProfileImageUrl = student.getProfileImage().getUrl();
        return USER_PROFILE_DEFAULT_IMAGE.getUrl().equals(studentProfileImageUrl);
    }
}
