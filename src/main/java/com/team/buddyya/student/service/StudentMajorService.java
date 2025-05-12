package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Major;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.StudentMajor;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.MajorRepository;
import com.team.buddyya.student.repository.StudentMajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentMajorService {

    private final MajorRepository majorRepository;
    private final StudentMajorRepository studentMajorRepository;

    public void createStudentMajors(List<String> majors, Student student) {
        majors.forEach(majorName -> {
            Major major = majorRepository.findByMajorName(majorName)
                    .orElseThrow(() -> new StudentException(StudentExceptionType.MAJOR_NOT_FOUND));
            StudentMajor studentMajor = StudentMajor.builder()
                    .student(student)
                    .major(major)
                    .build();
            studentMajor.setStudent(student);
            studentMajorRepository.save(studentMajor);
        });
    }
}
