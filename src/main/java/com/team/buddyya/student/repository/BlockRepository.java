package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Block;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByStudentAndBlockedStudentId(Student student, Long blockedStudentId);

    List<Block> findByStudentId(Long studentId);
}
