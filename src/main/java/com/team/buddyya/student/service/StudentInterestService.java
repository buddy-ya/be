package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Interest;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.StudentInterest;
import com.team.buddyya.student.exception.StudentException;
import com.team.buddyya.student.exception.StudentExceptionType;
import com.team.buddyya.student.repository.InterestRepository;
import com.team.buddyya.student.repository.StudentInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentInterestService {

    private final StudentInterestRepository studentInterestRepository;
    private final InterestRepository interestRepository;

    public void createStudentInterests(List<String> interests, Student student) {
        interests.forEach(interestName -> {
            Interest interest = interestRepository.findByInterestName(interestName)
                    .orElseThrow(() -> new StudentException(StudentExceptionType.INTEREST_NOT_FOUND));
            StudentInterest studentInterest = StudentInterest.builder()
                    .student(student)
                    .interest(interest)
                    .build();

            studentInterest.setStudent(student);

            studentInterestRepository.save(studentInterest);
        });
    }

    public void updateStudentInterests(List<String> interests, Student student) {
        studentInterestRepository.deleteByStudent(student);
        createStudentInterests(interests, student);
    }
}
