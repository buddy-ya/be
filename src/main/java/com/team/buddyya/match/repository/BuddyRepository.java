package com.team.buddyya.match.repository;

import com.team.buddyya.match.domain.MatchedHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BuddyRepository extends JpaRepository<MatchedHistory, Long> {

    @Query("SELECT b.buddyId FROM MatchedHistory b WHERE b.student.id = :studentId")
    Set<Long> findBuddyIdsByStudentId(Long studentId);
}
