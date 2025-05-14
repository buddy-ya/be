package com.team.buddyya.student.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.*;


class CharacterProfileImageTest {

    @DisplayName("getRandomProfileImage()는 항상 CharacterProfileImage 중 하나를 반환한다")
    @RepeatedTest(10)
    void getRandomProfileImage_항상_enum_중_하나를_반환한다() {
        CharacterProfileImage result = CharacterProfileImage.getRandomProfileImage();

        assertThat(result).isNotNull();

        assertThat(CharacterProfileImage.values()).contains(result);
    }
}