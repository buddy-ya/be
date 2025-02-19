package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.StudentMajor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentMajorRepository extends JpaRepository<StudentMajor, Long> {

    void deleteByStudent(Student student);
}
