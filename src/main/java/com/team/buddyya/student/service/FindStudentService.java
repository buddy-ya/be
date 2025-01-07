package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FindStudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public Student findByStudentId(long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentException(StudentExceptionType.STUDENT_NOT_FOUND));
    }
}
