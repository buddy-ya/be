package com.team.buddyya.auth.service;

import com.team.buddyya.auth.domain.CustomUserDetails;
import com.team.buddyya.auth.domain.StudentInfo;
import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.service.FindStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final FindStudentService findStudentService;


    @Override
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Student student = findStudentService.findByStudentId(Long.parseLong(id));
        StudentInfo studentInfo = StudentInfo.from(student);
        return new CustomUserDetails(studentInfo);
    }
}