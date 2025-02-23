package com.team.buddyya.match.repositorry;

import com.team.buddyya.match.domain.Buddy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BuddyRepository extends JpaRepository<Buddy, Long> {

    @Query("SELECT b.buddyId FROM Buddy b WHERE b.student.id = :studentId")
    Set<Long> findBuddyIdsByStudentId(Long studentId);
}
