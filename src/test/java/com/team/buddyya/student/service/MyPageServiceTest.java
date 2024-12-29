package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.student.domain.*;
import com.team.buddyya.student.dto.response.MyPageResponse;
import com.team.buddyya.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MyPageServiceTest {

    @InjectMocks
    private MyPageService myPageService;

    @Mock
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    private Student mockStudent;

    @BeforeEach
    void setUp() {
        // Mock 객체 생성
        University mockUniversity = mock(University.class);
        when(mockUniversity.getUniversityName()).thenReturn("sju");

        ProfileImage mockProfileImage = mock(ProfileImage.class);
        when(mockProfileImage.getUrl()).thenReturn("http://mock-image.com/profile.jpg");

        mockStudent = mock(Student.class);
        when(mockStudent.getName()).thenReturn("Hong gildong");
        when(mockStudent.getPhoneNumber()).thenReturn("01012345678");
        when(mockStudent.getCountry()).thenReturn("ko");
        when(mockStudent.getIsKorean()).thenReturn(true);
        when(mockStudent.getRole()).thenReturn(Role.STUDENT);
        when(mockStudent.getUniversity()).thenReturn(mockUniversity);
        when(mockStudent.getGender()).thenReturn(Gender.MALE);

        // 빈 리스트 설정
        when(mockStudent.getMajors()).thenReturn(List.of());
        when(mockStudent.getLanguages()).thenReturn(List.of());
        when(mockStudent.getInterests()).thenReturn(List.of());
        when(mockStudent.getProfileImage()).thenReturn(mockProfileImage);

        // StudentService Mock 설정
        when(studentService.findByStudentId(1L)).thenReturn(mockStudent);
    }

    @Test
    void 마이페이지_학생_정보를_조회한다() {
        // Given
        StudentInfo studentInfo = new StudentInfo(1L, Role.STUDENT, true);

        // When
        MyPageResponse response = myPageService.getMyPage(studentInfo);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Hong gildong");
        assertThat(response.country()).isEqualTo("ko");
    }
}