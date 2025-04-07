package com.team.buddyya.student.domain;

import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CharacterProfileImage {
    CHARACTER_PROFILE_IMAGE_1("image1", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__1.png"),
    CHARACTER_PROFILE_IMAGE_2("image2", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__2.png"),
    CHARACTER_PROFILE_IMAGE_3("image3", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__3.png"),
    CHARACTER_PROFILE_IMAGE_4("image4", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__4.png"),
    CHARACTER_PROFILE_IMAGE_5("image5", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__5.png"),
    CHARACTER_PROFILE_IMAGE_6("image6", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__6.png"),
    CHARACTER_PROFILE_IMAGE_7("image7", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__7.png"),
    CHARACTER_PROFILE_IMAGE_8("image8", "https://buddyya.s3.ap-northeast-2.amazonaws.com/default-profile-image/image__8.png");

    private final String key;
    private final String url;

    CharacterProfileImage(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public static CharacterProfileImage getRandomProfileImage() {
        CharacterProfileImage[] values = CharacterProfileImage.values();
        return values[(int) (Math.random() * values.length)];
    }

    public static CharacterProfileImage fromValue(String key) {
        return Arrays.stream(CharacterProfileImage.values())
                .filter(image -> image.key.equals(key))
                .findFirst()
                .orElseThrow(() -> new StudentException(StudentExceptionType.INVALID_DEFAULT_IMAGE_KEY));
    }
}

