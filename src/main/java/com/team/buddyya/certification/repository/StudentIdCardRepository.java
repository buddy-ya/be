package com.team.buddyya.certification.repository;

import com.team.buddyya.certification.domain.StudentIdCard;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface StudentIdCardRepository extends JpaRepository<StudentIdCard, Long> {

    Optional<StudentIdCard> findByStudent(Student student);

    List<StudentIdCard> findAllByOrderByCreatedDateAsc();

    void deleteByStudent(Student student);

    Optional<StudentIdCard> findByStudent_Id(Long studentId);

    @Query("SELECT s FROM StudentIdCard s WHERE s.rejectionReason IS NULL ORDER BY s.createdDate ASC")
    List<StudentIdCard> findUploadStudentIdCards();
}
