package com.team.buddyya.student.domain;

import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProfileDefaultImage {
    PROFILE_DEFAULT_IMAGE_1("image1", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image1.png"),
    PROFILE_DEFAULT_IMAGE_2("image2", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image2.png"),
    PROFILE_DEFAULT_IMAGE_3("image3", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image3.png"),
    PROFILE_DEFAULT_IMAGE_4("image4", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image4.png"),
    PROFILE_DEFAULT_IMAGE_5("image5", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image5.png"),
    PROFILE_DEFAULT_IMAGE_6("image6", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image6.png");

    private final String key;
    private final String url;

    ProfileDefaultImage(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public static ProfileDefaultImage getRandomProfileImage() {
        ProfileDefaultImage[] values = ProfileDefaultImage.values();
        return values[(int) (Math.random() * values.length)];
    }

    public static ProfileDefaultImage fromValue(String key) {
        return Arrays.stream(ProfileDefaultImage.values())
                .filter(image -> image.key.equals(key))
                .findFirst()
                .orElseThrow(() -> new StudentException(StudentExceptionType.INVALID_DEFAULT_IMAGE_KEY));
    }
}

