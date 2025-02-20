package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Block;
import com.team.buddyya.student.domain.Student;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerAndBlockedStudentId(Student blocker, Long blockedStudentId);

    @Query("select b.blockedStudentId from Block b where b.blocker.id = :blockerId")
    Set<Long> findBlockedStudentIdByBlockerId(@Param("blockerId") Long blockerId);

}
