package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.StudentInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentInterestRepository extends JpaRepository<StudentInterest, Long> {

    void deleteByStudent(Student student);
}

