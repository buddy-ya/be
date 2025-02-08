package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Block;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerAndBlockedStudentId(Student blocker, Long blockedStudentId);
}
