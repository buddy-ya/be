package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Student;
import com.team.buddyya.student.domain.StudentLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentLanguageRepository extends JpaRepository<StudentLanguage, Long> {

    void deleteByStudent(Student student);

    boolean existsByStudentAndLanguage_Id(Student student, Long languageId);
}
