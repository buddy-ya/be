package com.team.buddyya.student.service;

import com.team.buddyya.student.domain.Interest;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.StudentInterest;
import com.team.buddyya.student.dto.request.OnBoardingRequest;
import com.team.buddyya.student.exception.OnBoardingException;
import com.team.buddyya.student.exception.OnBoardingExceptionType;
import com.team.buddyya.student.repository.InterestRepository;
import com.team.buddyya.student.repository.StudentInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentInterestService {

    private final StudentInterestRepository studentInterestRepository;
    private final InterestRepository interestRepository;

    public void createStudentInterests(OnBoardingRequest request, Student student) {
        request.interests().forEach(interestName -> {
            Interest interest = interestRepository.findByInterestName(interestName)
                    .orElseThrow(() -> new OnBoardingException(OnBoardingExceptionType.INTEREST_NOT_FOUND));
            studentInterestRepository.save(StudentInterest.builder()
                    .student(student)
                    .interest(interest)
                    .build());
        });
    }
}
