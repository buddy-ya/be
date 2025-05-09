package com.team.buddyya.certification.domain;

import com.team.buddyya.student.domain.Gender;
import com.team.buddyya.student.domain.Role;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StudentIdCardTest {

    private Student dummyStudent;

    @BeforeEach
    void setUp() {
        University dummyUniv = mock(University.class);
        dummyStudent = Student.builder()
                .name("user1")
                .phoneNumber("01012345678")
                .country("ko")
                .isKorean(true)
                .role(Role.STUDENT)
                .university(dummyUniv)
                .gender(Gender.MALE)
                .characterProfileImage("imageUrl.png")
                .build();
    }

    @Test
    @DisplayName("빌더로 student id card 생성 시 imageUrl과 student는 정상 설정되고 rejectionResason은 null이어야 한다")
    void builder_set_image_url_and_student_and_rejection_reason_is_null() {
        //given
        String imageUrl = "http://test-url";

        //when
        StudentIdCard studentIdCard = StudentIdCard.builder()
                .imageUrl(imageUrl)
                .student(dummyStudent)
                .build();

        //then
        assertThat(studentIdCard.getImageUrl()).isEqualTo(imageUrl);
        assertThat(studentIdCard.getRejectionReason()).isNull();
        assertThat(studentIdCard.getStudent()).isSameAs(dummyStudent);
    }

    @Test
    @DisplayName("updateRejectionReason 호출 시 rejectionReason이 설정되어야 한다")
    void update_rejection_reason_set_reason() {
        //given
        StudentIdCard studentIdCard = StudentIdCard.builder()
                .imageUrl("image-url")
                .student(dummyStudent)
                .build();

        //when
        String reason = "학교 정보가 확인되지 않습니다";
        studentIdCard.updateRejectionReason(reason);

        //then
        assertThat(studentIdCard.getRejectionReason()).isNotNull();
        assertThat(studentIdCard.getRejectionReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("updateImageUrl 호출 시 imageUrl만 변경되고 rejectionReason은 null로 초기화 된다")
    void update_image_url_reset_rejection_reason() {
        //given
        StudentIdCard studentIdCard = StudentIdCard.builder()
                .imageUrl("old-url")
                .student(dummyStudent)
                .build();
        studentIdCard.updateRejectionReason("학교명이 보이지 않습니다");

        //when
        studentIdCard.updateImageUrl("new-url");

        //then
        assertThat(studentIdCard.getImageUrl()).isEqualTo("new-url");
        assertThat(studentIdCard.getRejectionReason()).isNull();
    }
}
