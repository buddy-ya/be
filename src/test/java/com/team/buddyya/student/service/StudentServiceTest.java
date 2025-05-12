package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Gender;
import com.team.buddyya.student.domain.Role;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class StudentServiceTest {

    @InjectMocks
    private StudentService studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Test
    void 정상적으로_Student를_생성한다() {
        // given
        OnBoardingRequest request = new OnBoardingRequest(
                "John", "us", false, true, "01012345678",
                "male", "sju",
                List.of("humanities"), List.of("ko"), List.of("kpop")
        );

        University mockUniversity = University.builder().universityName("sju").isActive(true).build();
        when(universityRepository.findByUniversityName("sju")).thenReturn(Optional.of(mockUniversity));

        Student mockStudent = Student.builder()
                .name("John")
                .university(mockUniversity)
                .role(Role.STUDENT)
                .gender(Gender.MALE)
                .characterProfileImage("img.png")
                .build();
        when(studentRepository.save(any())).thenReturn(mockStudent);

        // when
        Student result = studentService.createStudent(request);

        // then
        assertEquals("John", result.getName());
        assertEquals("sju", result.getUniversity().getUniversityName());
        assertEquals(Role.STUDENT, result.getRole());
    }

    @Test
    void 존재하지_않는_대학이면_예외를_던진다() {
        // given
        OnBoardingRequest request = new OnBoardingRequest(
                "Jane", "us", false, true, "01098765432",
                "female", "nonexistent-univ",
                List.of("social_sciences"), List.of("en"), List.of("movie")
        );
        when(universityRepository.findByUniversityName("nonexistent-univ"))
                .thenReturn(Optional.empty());

        // when & then
        StudentException exception = assertThrows(StudentException.class, () -> {
            studentService.createStudent(request);
        });

        assertEquals(StudentExceptionType.UNIVERSITY_NOT_FOUND, exception.exceptionType());
    }
}