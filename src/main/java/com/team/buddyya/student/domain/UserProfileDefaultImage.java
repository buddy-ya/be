package com.team.buddyya.student.domain;

import lombok.Getter;

@Getter
public enum UserProfileDefaultImage {

    USER_PROFILE_DEFAULT_IMAGE("userProfileDefaultImage",
            "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/user_profile_default_image.png");

    private final String key;
    private final String url;

    UserProfileDefaultImage(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public static boolean isDefaultUserProfileImage(Student student) {
        String userProfileImageUrl = student.getProfileImage().getUrl();
        return USER_PROFILE_DEFAULT_IMAGE.getUrl().equals(userProfileImageUrl);
    }

    public static boolean isProfileImageUpload(Student student) {
        String userProfileImageUrl = student.getProfileImage().getUrl();
        return !USER_PROFILE_DEFAULT_IMAGE.getUrl().equals(userProfileImageUrl);
    }

    public static String getChatroomProfileImage(Student student) {
        if (isDefaultUserProfileImage(student)) {
            return student.getProfileImage().getUrl();
        }
        return student.getCharacterProfileImage();
    }
}
