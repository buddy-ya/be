package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.*;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.exception.OnBoardingException;
import com.team.buddyya.student.exception.OnBoardingExceptionType;
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
                .orElseThrow(() -> new OnBoardingException(OnBoardingExceptionType.UNIVERSITY_NOT_FOUND));
        Student student = Student.builder()
                .name(request.name())
                .phoneNumber(request.phoneNumber())
                .major(request.major())
                .country(request.country())
                .isKorean(request.korean())
                .role(Role.STUDENT)
                .university(university)
                .gender(Gender.fromValue(request.gender()))
                .build();
        return studentRepository.save(student);
    }
}
