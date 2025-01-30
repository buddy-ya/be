package com.team.buddyya.student.service;

import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.student.domain.Gender;
import com.team.buddyya.student.domain.Role;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import com.team.buddyya.student.repository.UniversityRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UniversityRepository universityRepository;
    private final FindStudentService findStudentService;

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

    @Transactional(readOnly = true)
    public boolean isDuplicateStudentNumber(String studentNumber, University university) {
        return studentRepository.findByStudentNumberAndUniversity(studentNumber, university)
                .isPresent();
    }

    public void updateStudentCertification(Student student, String studentNumber) {
        student.updateIsCertificated(true);
        student.updateStudentNumber(studentNumber);
    }

    @Transactional(readOnly = true)
    public boolean isDuplicateStudentEmail(String email) {
        return studentRepository.findByEmail(email)
                .isPresent();
    }

    public void deleteStudent(StudentInfo studentInfo) {
        Student student = findStudentService.findByStudentId(studentInfo.id());
        String randomPhoneNumber = "deleted_" + UUID.randomUUID().toString().substring(0, 3);
        String randomEmail = "deleted_" + UUID.randomUUID().toString().substring(0, 4);
        student.updatePhoneNumber(randomPhoneNumber);
        student.updateEmail(randomEmail);
        student.updateIsCertificated(false);
    }
}
