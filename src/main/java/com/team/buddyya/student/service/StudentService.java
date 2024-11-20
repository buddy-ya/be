package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.*;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UniversityRepository universityRepository;

    public Student createStudent(OnBoardingRequest request) {
        University university = universityRepository.findByUniversityName(request.university())
                .orElseThrow(() -> new StudentException(StudentExceptionType.UNIVERSITY_NOT_FOUND));
        Student student = Student.builder()
                .name(request.name())
                .phoneNumber(request.phoneNumber())
                .country(request.country())
                .isKorean(request.isKorean())
                .role(Role.STUDENT)
                .university(university)
                .gender(Gender.fromValue(request.gender()))
                .build();
        return studentRepository.save(student);
    }
}
