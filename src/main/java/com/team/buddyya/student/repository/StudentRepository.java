package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByPhoneNumber(String phoneNumber);

    Optional<Student> findByStudentNumberAndUniversity(String studentNumber, University university);
}
