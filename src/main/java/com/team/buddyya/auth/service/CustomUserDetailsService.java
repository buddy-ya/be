package com.team.buddyya.auth.service;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.auth.dto.LoginStudentInfo;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.exception.OnBoardingException;
import com.team.buddyya.student.exception.OnBoardingExceptionType;
import com.team.buddyya.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Student student = studentRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new OnBoardingException(OnBoardingExceptionType.STUDENT_NOT_FOUND));
        LoginStudentInfo studentInfo = LoginStudentInfo.from(student);
        return new CustomUserDetails(studentInfo);
    }
}