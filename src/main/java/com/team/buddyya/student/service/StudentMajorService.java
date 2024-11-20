package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.*;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.MajorRepository;
import com.team.buddyya.student.repository.StudentMajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentMajorService {

    private final MajorRepository majorRepository;
    private final StudentMajorRepository studentMajorRepository;

    public void createStudentMajors(OnBoardingRequest request, Student student) {
        request.majors().forEach(majorName -> {
            Major major = majorRepository.findByMajorName(majorName)
                    .orElseThrow(() -> new StudentException(StudentExceptionType.MAJOR_NOT_FOUND));
            studentMajorRepository.save(StudentMajor.builder()
                    .student(student)
                    .major(major)
                    .build());
        });
    }
}
